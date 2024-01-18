package ls.jooq.field

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

/**
 * Creates an IN statement with [values] inlined. Useful for large collections and also if you're curious about the
 * generated SQL, because it's easier to read than the default `in` method.
 *
 * @param values the values to be inlined
 * @return a [Condition] that can be used in a `where` clause.
 * @throws IllegalArgumentException if [values] is empty
 */
fun Field<*>.containedIn(values: Collection<*>): Condition {
    require(values.isNotEmpty()) { "values in an IN condition can not be empty" }
    return DSL.inlined(`in`(values))
}
