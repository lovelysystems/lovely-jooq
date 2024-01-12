package ls.jooq

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.types.DayToSecond
import org.jooq.types.YearToMonth
import java.time.temporal.Temporal

/**
 * Creates an IN statement with [values] inlined. Useful for large collections and also if you're curious about the
 * generated SQL, because it's easier to read than the default `in` method.
 *
 * @param values the values to be inlined
 * @return a [Condition] that can be used in a `where` clause.
 */
fun Field<*>.containedIn(values: Collection<*>): Condition = DSL.inlined(`in`(values))

/**
 * A shorthand to use a Kotlinish flavor instead of writing `::class.java` every time.
 * Watch out, it's unsafe on the DB side, the fact that the compiler won't complain doesn't mean that the DB will be able
 * to cast the field to the desired type.
 *
 * @param T the desired type
 * @return a [Field] casted to the desired type.
 */
inline fun <reified T> Field<*>.castAs(): Field<T> = cast(T::class.java)

/**
 * Generates a subtraction with a standard "day to second" interval
 *
 * @param T the Temporal type of the field
 * @param days the number of days to subtract
 * @return a [Field] with the result of the subtraction using a DAY TO SECOND interval
 */
fun <T : Temporal> Field<T>.minusDays(days: Int): Field<T> = this.minus(DayToSecond(days))

/**
 * Generates a subtraction with a standard "year to month" interval
 *
 * @param T the Temporal type of the field
 * @param months the number of months to subtract
 * @return a [Field] with the result of the subtraction using a YEAR TO MONTH interval
 */
fun <T : Temporal> Field<T>.minusMonths(months: Int): Field<T> = this.minus(YearToMonth(0, months))

/**
 * Creates a field with `REGEXP_MATCHES` inside. The returned type will be an array of strings.
 * @see <a href="https://www.postgresql.org/docs/8.4/functions-matching.html">PostgreSQL docs</a>
 *
 * @param regex the regex to match
 * @param flags the flags to use
 * @return a [Field] with a regexp_matches call where
 */
fun Field<*>.regexpMatches(regex: String, flags: String): Field<Array<String>> =
    DSL.field("regexp_matches({0}, {1}, {2})", SQLDataType.VARCHAR.array(), this, DSL.inline(regex), flags)
