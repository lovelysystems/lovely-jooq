import com.lovelysystems.db.testing.PGTestSettings
import com.lovelysystems.db.testing.PGTestSetup
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.r2dbc.spi.ConnectionFactories
import org.jooq.DSLContext
import org.jooq.impl.DSL
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object DBExtension : TestListener, ConstructorExtension {

    /**
     * The non-blocking, R2DBC [DSLContext] to be used in the tests
     */
    lateinit var dslContext: DSLContext

    lateinit var pgSetup: PGTestSetup

    private const val DB_IMAGE_NAME = "postgres:15"

    override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
        clazz.findAnnotation<DBTest>()?.let {
            pgSetup = PGTestSettings(
                clientImage = DB_IMAGE_NAME,
                serverImage = DB_IMAGE_NAME,
                resetScripts = listOf("/pgdev/reset.sql"),
                serverConfiguration = {
                    addExposedPort(5432)
                }
            ).create()

            pgSetup.start()
            pgSetup.reset()

            // Using an R2DBC connection for the tests
            val port = pgSetup.server.getMappedPort(5432)
            val host = pgSetup.server.host
            dslContext = DSL.using(
                ConnectionFactories.get("r2dbc:postgresql://postgres:postgres@$host:$port/postgres"),
            )
        }
        return null
    }

    /**
     * Since a DB container will be created for every test class, we can safely stop them after each [Spec]
     */
    override suspend fun afterSpec(spec: Spec) {
        if (this::pgSetup.isInitialized) {
            pgSetup.server.stop()
            pgSetup.client.stop()
        }
    }
}

/**
 * Annotation which activates the postgres database via the [DBExtension]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DBTest
