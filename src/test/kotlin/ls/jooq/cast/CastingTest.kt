package ls.jooq.cast

import DBExtension
import DBTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirst
import ls.jooq.db.generated.Tables
import ls.jooq.db.generated.tables.records.AuthorRecord
import ls.jooq.db.generated.tables.records.BookRecord
import ls.jooq.execute.create

@DBTest
class CastingTest : FreeSpec({

    val ctx = DBExtension.dslContext

    "Record.mapTo<T>()" - {

        "should map the given record to the desired type - created by hand" {
            val record = AuthorRecord().apply {
                id = 1
                firstName = "foo"
            }
            record.mapTo<AuthorPojo>() shouldBe AuthorPojo("foo", 1)
        }

        "should map the given record to the desired type - coming from DB with field alias" {
            val record = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }

            val recordFromSelect = ctx
                .select(
                    Tables.AUTHOR.ID,
                    Tables.AUTHOR.FIRST_NAME.`as`("name")
                )
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(record.id))
                .awaitFirst()
                .mapTo<AuthorPojoWithAnotherField>()

            recordFromSelect shouldBe AuthorPojoWithAnotherField("foo", 1)
        }
    }

    "Field<*>.castAs()" - {

        "should cast the field to the desired type" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }
            val record = ctx.create<BookRecord> {
                authorId = author.id
                title = "foo"
                pages = 42
            }

            val selectWithCasting = ctx
                .select(Tables.BOOK.ID)
                .from(Tables.BOOK)
                .where(Tables.BOOK.PAGES.castAs<Long>().eq(42L))

            selectWithCasting.sql shouldBe """
                select "test"."book"."id" from "test"."book" where cast("test"."book"."pages" as bigint) = ?
             """.trimIndent()
            selectWithCasting.bindValues shouldBe listOf(42L)

            selectWithCasting.awaitFirst().value1() shouldBe record.id
        }
    }
})

data class AuthorPojo(
    val firstName: String,
    val id: Int
)

data class AuthorPojoWithAnotherField(
    val name: String,
    val id: Int
)
