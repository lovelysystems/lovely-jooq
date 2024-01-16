package ls.jooq.field

import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.types.DayToSecond
import org.jooq.types.YearToMonth
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.Temporal

/**
 * Generates a subtraction with a standard "day to second" interval
 *
 * @param T the Temporal type of the field
 * @param days the number of days to subtract
 * @return a [Field] with the result of the subtraction using a DAY TO SECOND interval
 */
fun <T : Temporal> Field<T>.minusDays(days: Int): Field<T> = this.minus(DayToSecond(days))

/**
 * Generates an addition with a standard "day to second" interval
 *
 * @param T the Temporal type of the field
 * @param days the number of days to add
 * @return a [Field] with the result of the addition using a DAY TO SECOND interval
 */
fun <T : Temporal> Field<T>.plusDays(days: Int): Field<T> = this.plus(DayToSecond(days))

/**
 * Generates a subtraction with a standard "year to month" interval
 *
 * @param T the Temporal type of the field
 * @param months the number of months to subtract
 * @return a [Field] with the result of the subtraction using a YEAR TO MONTH interval
 */
fun <T : Temporal> Field<T>.minusMonths(months: Int): Field<T> = this.minus(YearToMonth(0, months))

/**
 * Generates an addition with a standard "year to month" interval
 *
 * @param T the Temporal type of the field
 * @param months the number of months to add
 * @return a [Field] with the result of the addition using a YEAR TO MONTH interval
 */
fun <T : Temporal> Field<T>.plusMonths(months: Int): Field<T> = this.plus(YearToMonth(0, months))

/**
 * Creates a field with `REGEXP_MATCHES` inside. The returned type will be an array of strings.
 * @see <a href="https://www.postgresql.org/docs/8.4/functions-matching.html">PostgreSQL docs</a>
 *
 * @param regex the regex to match
 * @param flags the flags to use
 * @return a [Field] with a regexp_matches call where
 */
fun Field<*>.regexpMatches(regex: String, flags: String): Field<Array<String>?> =
    DSL.field("regexp_matches({0}, {1}, {2})", SQLDataType.VARCHAR.array(), this, DSL.inline(regex), flags)

/**
 * Converts the given [Instant] to a [Field] of type [OffsetDateTime] using the `TO_TIMESTAMP` function.
 *
 * @return a [Field] with a TO_TIMESTAMP call in there

 */
fun Instant.asTSField(): Field<OffsetDateTime> {
    return DSL.field("TO_TIMESTAMP({0})", OffsetDateTime::class.java, this.epochSecond)
}
