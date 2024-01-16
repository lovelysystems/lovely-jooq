import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FreeSpec
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*

/**
 * This test generates the jOOQ code from the test database schema. Need to be run manually only if the schema in
 * [DBExtension] changes.
 * The easies workflow is to run the schema initialization script on a local DB instance and then run this test against it.
 */
@Ignored
class JooqTestSchemaGenerator : FreeSpec({

    "should generate jooq code" {

        // Change it if the configuration below differs from your local DB
        val url = "jdbc:postgresql://localhost:25432/postgres"
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
