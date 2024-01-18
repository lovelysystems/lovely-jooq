package ls.jooq.field

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ls.jooq.db.generated.Tables

class ConditionsTest : FreeSpec({

    "Field<*>.containedIn()" - {

        "should create an IN statement with the given values inlined" {
            val field = Tables.AUTHOR.ID
            val values = listOf(1, 2, 3)

            val condition = field.containedIn(values)

            // Formatting is important here, indenting the expected string will break the test
            condition.toString() shouldBe """"test"."author"."id" in (
  1, 2, 3
)"""
        }

        "should throw an exception if the given values are empty" {
            val field = Tables.AUTHOR.ID
            val values = emptyList<Int>()

            val exception = shouldThrow<IllegalArgumentException> {
                field.containedIn(values)
            }

            exception.message shouldBe "values in an IN condition can not be empty"
        }
    }
})
