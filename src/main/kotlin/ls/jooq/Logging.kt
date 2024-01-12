package ls.jooq

import mu.KLogger
import org.jooq.AttachableQueryPart
import org.jooq.conf.ParamType

/**
 * Logs the given jooq [query] with parameters inlined at trace level.
 *
 * @param query the query to log
 * @param name the name to use when logging the query
 */
fun KLogger.traceSQL(query: AttachableQueryPart, name: String = "QUERY") {
    if (isTraceEnabled) {
        val sql = query.getSQL(ParamType.INLINED)
        trace { "$name: $sql" }
    }
}
