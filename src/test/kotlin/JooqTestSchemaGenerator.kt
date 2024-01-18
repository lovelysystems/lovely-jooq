import DBExtension.pgSetup
import io.kotest.core.spec.style.FreeSpec
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*

/**
 * This test generates the jOOQ code from the test database schema. If you changed the test schema, you should definitely
 * run this to make the generated jOOQ code up to date.
 */
@DBTest
class JooqTestSchemaGenerator : FreeSpec({

    "should generate jooq code" {

        val port = pgSetup.server.getMappedPort(5432)
        val host = pgSetup.server.host
        val url = "jdbc:postgresql://$host:$port/postgres"
        val username = "postgres"
        val password = "postgres"

        GenerationTool.generate(
            Configuration()
                .withJdbc(
                    Jdbc()
                        .withDriver("org.postgresql.Driver")
                        .withUrl(url)
                        .withUser(username)
                        .withPassword(password)
                )
                .withGenerator(
                    // use the kotlin generator
                    Generator()
                        //.withName("org.jooq.codegen.KotlinGenerator")
                        .withDatabase(
                            Database()
                                .withIncludeSequences(false)
                                .withInputSchema("test")
                                .withIncludeSequences(false)
                                .withIncludeRoutines(false)
                        )
                        .withGenerate(
                            Generate()
                                .withGlobalCatalogReferences(false)
                                .withKeys(false)
                                .withIndexes(false)
                                .withSequences(false)
                                .withDeprecated(false)
                                .withDaos(false) // no suspend functions
                                .withRecords(true)
                                .withPojos(false)
                                .withRecordsImplementingRecordN(true)
                                .withRoutines(false)
                                .withValidationAnnotations(false)
                                .withJpaAnnotations(false)
                        )
                        .withTarget(
                            org.jooq.meta.jaxb.Target()
                                .withPackageName("ls.jooq.db.generated")
                                .withDirectory("../java")
                        )
                )
        )
    }
})
