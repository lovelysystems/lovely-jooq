package ls.jooq.execute

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.Publisher
import org.jooq.Record
import org.jooq.Record1

/**
 * Collects the first value of each record into a list.
 *
 * @param T the type of the first value of each record.
 * @return a list of the first value of each record.
 */
suspend fun <T : Any> Publisher<Record1<T>>.awaitListOfFirsts(): List<T> = asFlow().map { it.value1() }.toList()

/**
 * Awaits the first value of the first record in the result set. Be aware that it's NOT SAFE to use it where the result
 * set can be empty.
 *
 * @param T the type of the first value of the first record.
 * @return the first value of the first record in the result set.
 */
suspend fun <T : Any> Publisher<Record1<T>>.awaitFirst(): T = awaitFirst().value1()

/**
 * Awaits the first value of the first record in the result set. Returns null if the result set is empty.
 *
 * @param T the type of the first value of the first record.
 * @return the first value of the first record in the result set or null if the result set is empty.
 */
suspend fun <T : Any> Publisher<Record1<T>>.awaitFirstOrNull(): T? = awaitFirstOrNull()?.value1()

/**
 * Collects all the records from the result set into a list.
 *
 * @param T the type the records should be mapped to.
 * @param R the type of the records in the result set.
 * @return a list of [T] objects.
 */
suspend inline fun <reified T, R : Record> Publisher<R>.awaitAll(): List<T> = asFlowOf<T, R>().toList()

/**
 * Returns the result set as a [Flow] of [T] objects.
 *
 * @param T the type the records should be mapped to.
 * @param R the type of the records in the result set.
 * @return a [Flow] of [T] objects.
 */
inline fun <reified T, R : Record> Publisher<R>.asFlowOf(): Flow<T> = asFlow().map { it.into(T::class.java) }
