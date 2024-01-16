package ls.jooq.execute

import DBExtension
import DBTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import ls.jooq.cast.AuthorPojoWithAnotherField
import ls.jooq.db.generated.Tables
import ls.jooq.db.generated.tables.records.AuthorRecord

@DBTest
class CoroutineBridgeTest : FreeSpec({

    val ctx = DBExtension.dslContext

    beforeSpec {
        ctx.create<AuthorRecord> {
            firstName = "foo"
            lastName = "bar"
        }
        ctx.create<AuthorRecord> {
            firstName = "baz"
            lastName = "biz"
        }
    }


    "awaitListOfFirsts()" - {

        "should return the first value of each record in the result set" {
            val result = ctx
                .select(Tables.AUTHOR.FIRST_NAME)
                .from(Tables.AUTHOR)
                .orderBy(Tables.AUTHOR.FIRST_NAME)
                .awaitListOfFirsts()

            result shouldBe listOf("baz", "foo")
        }
    }

    "awaitFirst()" - {

        "should return the first value of the first record in the result set" {
            val result = ctx
                .select(Tables.AUTHOR.FIRST_NAME)
                .from(Tables.AUTHOR)
                .orderBy(Tables.AUTHOR.FIRST_NAME)
                .awaitFirst()

            result shouldBe "baz"
        }
    }

    "awaitFirstOrNull()" - {

        "should return the first value of the first record in the result set" {
            val result = ctx
                .select(Tables.AUTHOR.FIRST_NAME)
                .from(Tables.AUTHOR)
                .orderBy(Tables.AUTHOR.FIRST_NAME)
                .awaitFirstOrNull()

            result shouldBe "baz"
        }

        "should return null if the result set is empty" {
            val result = ctx
                .select(Tables.AUTHOR.FIRST_NAME)
                .from(Tables.AUTHOR)
                .where(Tables.AUTHOR.FIRST_NAME.eq("does not exist"))
                .awaitFirstOrNull()

            result shouldBe null
        }
    }

    "awaitAll()" - {

        "should return all records in the result set mapped to the specified type" {
            val result: List<AuthorPojoWithAnotherField> = ctx
                .select(
                    Tables.AUTHOR.ID,
                    Tables.AUTHOR.FIRST_NAME.`as`("name")
                ).from(Tables.AUTHOR)
                .awaitAll()

            result.size shouldBe 2

            result.forOne { it.name shouldBe "baz" }
            result.forOne { it.name shouldBe "foo" }
        }
    }

    "asFlowOf()" - {

        "should return all records in the result set as a Flow of the specified type" {
            val result: Flow<AuthorPojoWithAnotherField> = ctx
                .select(
                    Tables.AUTHOR.ID,
                    Tables.AUTHOR.FIRST_NAME.`as`("name")
                ).from(Tables.AUTHOR)
                .asFlowOf()

            result.toList().let { resultAsList ->
                resultAsList.size shouldBe 2
                resultAsList.forOne { it.name shouldBe "baz" }
                resultAsList.forOne { it.name shouldBe "foo" }
            }
        }
    }
})
