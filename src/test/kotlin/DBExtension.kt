import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.r2dbc.spi.ConnectionFactories
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object DBExtension : TestListener, ConstructorExtension {

    private const val RESET_SCRIPT = """
        DROP SCHEMA IF EXISTS test CASCADE;
        CREATE SCHEMA test;
        
        CREATE TABLE test.author (
          id SERIAL NOT NULL,
          first_name VARCHAR(100) NOT NULL,
          last_name VARCHAR(100) NOT NULL,
          created TIMESTAMP with time zone NOT NULL DEFAULT now(),
          
          CONSTRAINT pk_author PRIMARY KEY (id)
        );
        
        CREATE TABLE test.book (
          id SERIAL NOT NULL,
          author_id INT NOT NULL,
          title VARCHAR(100) NOT NULL,
          pages INT2 NULL,
          
          CONSTRAINT pk_book PRIMARY KEY (id),
          CONSTRAINT fk_book_author FOREIGN KEY (id) REFERENCES test.author
        );
    """

    /**
     * The non-blocking, R2DBC [DSLContext] to be used in the tests
     */
    lateinit var dslContext: DSLContext

    private lateinit var postgresContainer: PostgreSQLContainer<*>

    override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
        clazz.findAnnotation<DBTest>()?.let {
            postgresContainer = PostgreSQLContainer("postgres:13")
            postgresContainer.start()

            // Running the init script on a blocking JDBC connection
            val blockingContext = DSL.using(
                postgresContainer.jdbcUrl,
                postgresContainer.username,
                postgresContainer.password
            )
            blockingContext.query(RESET_SCRIPT).execute()
            blockingContext.close()

            // Using an R2DBC connection for the tests
            dslContext = DSL.using(
                ConnectionFactories.get(PostgreSQLR2DBCDatabaseContainer.getOptions(postgresContainer))
            )
        }
        return null
    }

    /**
     * Since a DB container will be created for every test class, we can safely stop them after each [Spec]
     */
    override suspend fun afterSpec(spec: Spec) {
        if (this::postgresContainer.isInitialized && postgresContainer.isRunning) {
            postgresContainer.stop()
        }
    }
}

/**
 * Annotation which activates the postgres database via the [DBExtension]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DBTest
