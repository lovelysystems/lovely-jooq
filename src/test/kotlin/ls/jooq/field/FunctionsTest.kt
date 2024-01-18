package ls.jooq.field

import DBExtension
import DBTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameInstantAs
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import ls.jooq.db.generated.Tables
import ls.jooq.db.generated.tables.records.AuthorRecord
import ls.jooq.execute.create
import org.jooq.Publisher
import org.jooq.Record1
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DBTest
class FunctionsTest : FreeSpec({

    val ctx = DBExtension.dslContext

    "the [plus|minus][Days|Months] functions" - {

        "should modify the given field correctly" {
            val fixedCreation = OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
                created = fixedCreation
            }

            val resultWithModifiedDates = ctx
                .select(
                    Tables.AUTHOR.CREATED.plusDays(10),
                    Tables.AUTHOR.CREATED.plusMonths(1),
                    Tables.AUTHOR.CREATED.minusDays(5),
                    Tables.AUTHOR.CREATED.minusMonths(1),
                )
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitFirst()

            resultWithModifiedDates.value1() shouldHaveSameInstantAs fixedCreation.plusDays(10)
            resultWithModifiedDates.value2() shouldHaveSameInstantAs fixedCreation.plusMonths(1)
            resultWithModifiedDates.value3() shouldHaveSameInstantAs fixedCreation.minusDays(5)
            resultWithModifiedDates.value4() shouldHaveSameInstantAs fixedCreation.minusMonths(1)
        }
    }

    "the Field.regexpMatches() function" - {

        suspend fun <T : Any> Publisher<Record1<Array<T>?>>.awaitListOfArrays(): List<Array<T>?> =
            asFlow().map { it.value1() }.toList()

        "should generate the correct SQL" {
            val field = Tables.AUTHOR.FIRST_NAME
            val regex = "foo.*"
            val flags = "g"

            val result = field.regexpMatches(regex, flags)

            result.toString() shouldBe """regexp_matches("test"."author"."first_name", 'foo.*', 'g')"""
        }

        "should return the correct result from the DB" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foobarbequebazilbarfbonk"
                lastName = "somebarOther"
            }

            val result1 = ctx
                .select(
                    Tables.AUTHOR.FIRST_NAME.regexpMatches("(b[^b]+)(b[^b]+)", "g")
                )
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitListOfArrays()

            result1.shouldHaveSize(2)
            result1 shouldBe listOf(
                arrayOf("bar", "beque"),
                arrayOf("bazil", "barf")
            )

            val result2 = ctx
                .select(
                    Tables.AUTHOR.FIRST_NAME.regexpMatches("ping", "g")
                )
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitListOfArrays()

            result2.shouldHaveSize(0)

            val result3 = ctx
                .select(
                    Tables.AUTHOR.LAST_NAME.regexpMatches("bar", "")
                )
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitListOfArrays()

            result3.shouldHaveSize(1)
            result3.first() shouldBe arrayOf("bar")
        }
    }

    "Instant.asTSField()" - {

        "should generate the correct SQL" {
            val now = Instant.now()

            val result = now.asTSField()

            result.toString() shouldBe "TO_TIMESTAMP(${now.epochSecond})"
        }
    }
})
