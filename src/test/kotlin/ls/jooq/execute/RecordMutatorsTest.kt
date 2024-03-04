package ls.jooq.execute

import DBExtension
import DBTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitSingle
import ls.jooq.db.generated.Tables
import ls.jooq.db.generated.tables.records.AuthorRecord

@DBTest
class RecordMutatorsTest : FreeSpec({

    val ctx = DBExtension.dslContext

    "DSLContext.insertAndRefreshRecord" - {

        "should insert the record" {
            val record = AuthorRecord()
            record.firstName = "max"
            record.lastName = "irrelevant"
            ctx.insertAndRefreshRecord(record)

            val insertedContent = ctx.selectFrom(Tables.AUTHOR).awaitAll<AuthorRecord, _>().shouldHaveSize(1).first()
            insertedContent.firstName shouldBe "max"
            insertedContent.lastName shouldBe "irrelevant"
            insertedContent.id.shouldNotBeNull()
        }

        "after the call the record should have values set from database defaults" {
            val record = AuthorRecord()
            record.firstName = "max"
            record.lastName = "irrelevant"
            ctx.insertAndRefreshRecord(record)

            record.id.shouldNotBeNull()
            record.created.shouldNotBeNull()
        }
    }

    "DSLContext.create<T>()" - {

        "should create a record of the given type and insert it into the DB" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }

            val authorInDb = ctx
                .selectFrom(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitSingle()

            authorInDb.id shouldBe author.id
            authorInDb.firstName shouldBe "foo"
            authorInDb.lastName shouldBe "bar"
            authorInDb.created.shouldNotBeNull() // Default values from DB should be populated as well
        }
    }

    "DSLContext.updateAndExecute()" - {

        "should create an update statement for the given record and execute it" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }

            author.firstName = "baz"

            val updatedRows = ctx.updateAndExecute(author)

            updatedRows shouldBe 1

            val authorInDb = ctx
                .selectFrom(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitSingle()

            authorInDb.id shouldBe author.id
            authorInDb.firstName shouldBe "baz"
            authorInDb.lastName shouldBe author.lastName
        }
    }

    "DSLContext.updateIfChangedAndExecute()" - {

        "should execute update for the given record if it has been changed" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }

            author.firstName = "baz"

            val updatedRows = ctx.updateIfChangedAndExecute(author)

            updatedRows shouldBe 1

            val authorInDb = ctx
                .selectFrom(Tables.AUTHOR)
                .where(Tables.AUTHOR.ID.eq(author.id))
                .awaitSingle()

            authorInDb.id shouldBe author.id
            authorInDb.firstName shouldBe "baz"
            authorInDb.lastName shouldBe author.lastName
        }

        "should not execute update for the given record if it has not been changed" {
            val author = ctx.create<AuthorRecord> {
                firstName = "foo"
                lastName = "bar"
            }

            val updatedRows = ctx.updateIfChangedAndExecute(author)

            updatedRows shouldBe null
        }
    }
})
