package ls.jooq.cast

import org.jooq.Field
import org.jooq.Record

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
 * A shorthand to use a Kotlinish flavor instead of writing `::class.java` every time.
 * Watch out, it's unsafe on the DB side, the fact that the compiler won't complain doesn't mean that the DB will be able
 * to cast the field to the desired type.
 *
 * @param T the desired type
 * @return a [Field] cast to the desired type.
 */
inline fun <reified T> Field<*>.castAs(): Field<T> = cast(T::class.java)
