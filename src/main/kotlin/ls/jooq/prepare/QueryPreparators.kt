package ls.jooq.prepare

import edu.umd.cs.findbugs.annotations.CheckReturnValue
import org.jooq.*
import java.util.*

/**
 * Creates an insert query for the given [record].
 * Watch out: this will NOT execute the insert!
 *
 * @param R the type of the record
 * @param record the record to insert
 * @return the insert query
 */
@CheckReturnValue
fun <R : UpdatableRecord<R>> DSLContext.insert(record: UpdatableRecord<R>): InsertSetMoreStep<R> =
    insertInto(record.table).set(record)

/**
 * Creates an update statement to update the given [Record].
 * Watch out: this will NOT execute the update!
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the update statement
 * @throws IllegalStateException if the record has no primary key
 */
@CheckReturnValue
fun <R : UpdatableRecord<R>> DSLContext.update(record: R): UpdateQuery<R> {
    val query = updateQuery(record.table)
    val pk = checkNotNull(record.table.primaryKey) { "${record.table} has no primary key" }
    addConditions(query, record, *pk.fieldsArray)
    query.setRecord(record)
    return query
}

private fun <T> Field<T>.equalsOrIsNull(value: T?): Condition = if (value == null) isNull() else eq(value)

private fun <T> addCondition(query: UpdateQuery<*>, record: Record, field: Field<T>) {
    query.addConditions(field.equalsOrIsNull(record.get(field)))
}

private fun addConditions(query: UpdateQuery<*>, record: Record, vararg keys: Field<*>) {
    for (field in keys) addCondition(query, record, field)
}

/**
 * A simplified version of [Record.touched] that differentiates between real value changes and changes that actually set
 * the same value to a given field. The built-in [Record.touched] method consider every setter call on a record as a
 * change which might be not beneficial in most of our use cases.
 * Might be problematic to use with records that doesn't come from the database originally, mostly because the default
 * values wouldn't be there on the created record, which could lead to inconsistency. Recommended to use when you grab a
 * record from the DB, do something with it, and then you want to check if it has really changed before you execute an
 * actual update against the DB.
 *
 * You can read more about the reasons why the original method works like that here:
 * @see <a href="https://stackoverflow.com/a/38199329">https://stackoverflow.com/a/38199329</a>
 *
 * @return true if the record has changed, false otherwise
 */
@CheckReturnValue
fun Record.valuesChanged(): Boolean {
    for (i in 0..<size()) {
        if (touched(i) && !Objects.equals(original(i), get(i))) return true
    }
    return false
}
