package ls.jooq.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

class LoggingTest : FreeSpec({

    val memoryAppender = ListAppender<ILoggingEvent>()
    val logger = LoggerFactory.getLogger("ls") as Logger
    logger.level = Level.INFO

    beforeSpec {
        logger.addAppender(memoryAppender)
        memoryAppender.start()
    }

    "Logger.traceSQL()" - {

        "should not log anything if TRACE level is not enabled" {
            val query = DSL.select().from("foo")
            logger.traceSQL(query)

            memoryAppender.list.size shouldBe 0
        }

        "should log the given query with inlined parameters at TRACE level" {
            logger.level = Level.TRACE
            val query = DSL.select().from("foo").where(DSL.field("bar").eq("baz"))

            logger.traceSQL(query, "A nice query to log")

            memoryAppender.list.size shouldBe 1
            memoryAppender.list.first().message shouldBe "A nice query to log: select * from foo where bar = 'baz'"
        }
    }
})
