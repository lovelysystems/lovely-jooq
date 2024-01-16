package ls.jooq.prepare

import DBExtension
import DBTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirst
import ls.jooq.db.generated.Tables
import ls.jooq.db.generated.tables.records.AuthorRecord
import ls.jooq.db.generated.tables.records.BookRecord
import ls.jooq.execute.awaitAll
import ls.jooq.execute.create

@DBTest
class QueryPreparatorsTest : FreeSpec({

    val ctx = DBExtension.dslContext

    "Record.valuesChanged()" - {

        "should indicate a change only if there was a real change" - {

            "bare Record, instantiated by hand" {

                val record = BookRecord().apply {
                    title = "foo"
                }
                // Resetting the changed flag to start with a clean sheet
                record.changed(false)
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Doing the first update
                record.title = "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true // Built-in method should return true on purpose

                record.title = "bar"
                record.valuesChanged() shouldBe true
                record.changed() shouldBe true

                // Reverting the changes
                record.title = "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true

                // Resetting the changed flag to start with a clean sheet
                record.changed(false)
                record.title shouldBe "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Testing null handling
                record.title = null
                record.valuesChanged() shouldBe true
                record.changed() shouldBe true

                // Resetting the changed flag to start with a clean sheet
                record.changed(false)
                record.title shouldBe null
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Setting null again
                record.title = null
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true
            }

            "a Record that actually came from the DB and wasn't touched before" {

                val record = ctx.create<AuthorRecord> {
                    firstName = "foo"
                    lastName = "baz"
                }
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Doing the first update
                record.firstName = "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true // Built-in method should return true on purpose

                record.firstName = "bar"
                record.valuesChanged() shouldBe true
                record.changed() shouldBe true

                // Reverting the changes
                record.firstName = "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true

                // Resetting the changed flag to start with a clean sheet
                record.changed(false)
                record.firstName shouldBe "foo"
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Testing null handling
                record.firstName = null
                record.valuesChanged() shouldBe true
                record.changed() shouldBe true

                // Resetting the changed flag to start with a clean sheet
                record.changed(false)
                record.firstName shouldBe null
                record.valuesChanged() shouldBe false
                record.changed() shouldBe false

                // Setting null again
                record.firstName = null
                record.valuesChanged() shouldBe false
                record.changed() shouldBe true
            }
        }
    }

    "DSLContext.insert()" - {

        "should create an insert query for the given record" {

            val record = BookRecord().apply {
                title = "foo"
                pages = 123
            }
            val query = ctx.insert(record)
            query.sql shouldBe """insert into "test"."book" ("title", "pages") values (?, ?)"""
            query.bindValues shouldBe listOf("foo", 123)

            // Should not change anything in the DB
            val booksInDb: List<BookRecord> = ctx.selectFrom(Tables.BOOK).awaitAll()
            booksInDb.shouldBeEmpty()
        }
    }

    "DSLContext.update()" - {

        "should create an update query for the given record, including the PK condition automatically" {

            val record = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "baz"
            }
            record.firstName = "bar"
            val query = ctx.update(record)
            query.sql shouldBe """update "test"."author" set "first_name" = ? where "test"."author"."id" = ?"""
            query.bindValues shouldBe listOf("bar", record.id)

            // Should not change anything in the DB
            val authorInDb: AuthorRecord = ctx
                .selectFrom(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(record.id))
                .awaitFirst()

            authorInDb.firstName shouldBe "foo"
        }
    }
})
