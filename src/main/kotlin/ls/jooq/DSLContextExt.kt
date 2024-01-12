package ls.jooq

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.*

/**
 * Creates an insert query for the given [record]. Watch out: this will NOT execute the insert!
 *
 * @param R the type of the record
 * @param record the record to insert
 * @return the insert query
 */
fun <R : UpdatableRecord<R>> DSLContext.prepareInsert(record: UpdatableRecord<R>): InsertSetMoreStep<R> =
    insertInto(record.table).set(record)

/**
 * Creates a record of the given type and inserts it into the DB.
 *
 * @param R the type of the record
 * @param init a function to modify the record before insertion
 * @return the inserted record
 */
suspend inline fun <reified R : UpdatableRecord<R>> DSLContext.create(init: R.() -> Unit = {}): R {

    val constructor = R::class.java.getConstructor() ?: error("no default constructor found for ${R::class}")
    val record = constructor.newInstance()
    record.init()
    return prepareInsert(record).returning().awaitFirst()
}

/**
 * Creates an update statement to update the given [Record].
 * Watch out: this will NOT execute the update!
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the update statement
 */
fun <R : UpdatableRecord<R>> DSLContext.prepareUpdate(record: R): UpdateQuery<R> {
    val query = updateQuery(record.table)
    val pk = record.table.primaryKey ?: error("${record.table} has no primary key")
    addConditions(query, record, *pk.fieldsArray)
    query.setRecord(record)
    return query
}

/**
 * Creates an update statement for the given [Record] and also executes it.
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the number of updated rows
 */
suspend fun <R : UpdatableRecord<R>> DSLContext.update(record: R): Int {
    return prepareUpdate(record).awaitSingle()
}

/**
 * Executes update for the given [Record] if it has been changed.
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the number of updated rows or null if no update was necessary.
 */
suspend fun <R : UpdatableRecord<R>> DSLContext.updateIfChanged(record: R): Int? =
    if (record.valuesChanged()) {
        // without this guard: we get an error running the query
        // SQL [update "table" set [ no fields are updated ] where "table"."pk" = $1]; syntax error at or near "["
        update(record)
    } else null

private fun <T> condition(field: Field<T>, value: T?): Condition {
    return if (value == null) field.isNull() else field.eq(value)
}

private fun <T> addCondition(provider: UpdateQuery<*>, record: Record, field: Field<T>) {
    provider.addConditions(condition(field, record.get(field)));
}

private fun addConditions(query: UpdateQuery<*>, record: Record, vararg keys: Field<*>) {
    for (field in keys) addCondition(query, record, field)
}
