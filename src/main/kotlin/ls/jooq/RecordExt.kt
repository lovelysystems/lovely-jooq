package ls.jooq

import org.jooq.Record
import java.util.*

/**
 * Maps the record to the given type using [Record.into].
 * This method is named differently to make it more unique and avoid conflicts with jooq's own methods.
 *
 * @param T the desired type to map to
 * @return the mapped object
 */
inline fun <reified T : Any> Record.mapTo(): T {
    return this.into(T::class.java)
}

/**
 * A simplified version of [Record.changed] that differentiates between real value changes and changes that actually set
 * the same value to a given field. The built-in [Record.changed] method consider every setter call on a record as a
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
fun Record.valuesChanged(): Boolean {
    for (i in 0..<size()) {
        if (changed(i) && !Objects.equals(original(i), get(i))) return true
    }
    return false
}
