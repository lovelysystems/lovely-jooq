package ls.jooq.execute

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import ls.jooq.prepare.insert
import ls.jooq.prepare.update
import ls.jooq.prepare.valuesChanged
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.UpdatableRecord

/**
 * Creates a record of the given type and inserts it into the DB.
 *
 * @param R the type of the record
 * @param init a function to modify the record before insertion
 * @return the inserted record
 * @throws IllegalStateException if the record has no default constructor
 */
suspend inline fun <reified R : UpdatableRecord<R>> DSLContext.create(init: R.() -> Unit = {}): R {
    val constructor = checkNotNull(R::class.java.getConstructor()) { "no default constructor found for ${R::class}" }
    val record = constructor.newInstance()
    record.init()
    return insert(record).returning().awaitSingle()
}

/**
 * Creates an update statement for the given [Record] and also executes it.
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the number of updated rows
 */
suspend fun <R : UpdatableRecord<R>> DSLContext.updateAndExecute(record: R): Int = this.update(record).awaitSingle()

/**
 * Executes update for the given [Record] if it has been changed.
 *
 * @param R the type of the record
 * @param record the record to update
 * @return the number of updated rows or null if no update was necessary.
 */
suspend fun <R : UpdatableRecord<R>> DSLContext.updateIfChangedAndExecute(record: R): Int? =
    if (record.valuesChanged()) {
        // without this guard: we get an error running the query
        // SQL [update "table" set [ no fields are updated ] where "table"."pk" = $1]; syntax error at or near "["
        this.updateAndExecute(record)
    } else null

/**
 * Inserts a [record] and sets the values of [record] from the returned values of the insert
 *
 * @param R the type of record
 * @param record - the record to insert
 * @return the inserted record, note that also the param record will have updated values
 */
suspend fun <R : UpdatableRecord<R>> DSLContext.insertAndRefreshRecord(record: UpdatableRecord<R>): UpdatableRecord<R> {
    val inserted = insert(record).returning().awaitFirst()
    record.from(inserted)
    return record
}

suspend fun <R : UpdatableRecord<R>> DSLContext.upsert(record: R): R =
    insertInto(record.table)
        .set(record)
        .onDuplicateKeyUpdate()
        .set(record)
        .returning()
        .awaitFirst()

suspend inline fun <reified R : UpdatableRecord<R>> DSLContext.upsert(init: R.() -> Unit): R {
    val constructor = checkNotNull(R::class.java.getConstructor()) { "no default constructor found for ${R::class}" }
    val record = constructor.newInstance()
    record.init()
    return upsert(record)
}
