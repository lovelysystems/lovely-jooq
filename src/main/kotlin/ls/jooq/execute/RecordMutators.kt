package ls.jooq.execute

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
 */
suspend inline fun <reified R : UpdatableRecord<R>> DSLContext.create(init: R.() -> Unit = {}): R {
    val constructor = R::class.java.getConstructor() ?: error("no default constructor found for ${R::class}")
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
