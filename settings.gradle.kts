pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion

        id("com.lovelysystems.gradle") version "1.12.0"
        id("io.gitlab.arturbosch.detekt") version "1.23.4"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

/**
 * create lib aliases for each artifact and a bundle with the group name
 */
fun VersionCatalogBuilder.libsAndBundle(
    group: String,
    version: String,
    vararg artifacts: String,
    bundleName: String? = null,
) {
    artifacts.forEach {
        library(it, group, it).version(version)
    }
    bundleName?.let {
        bundle(it, artifacts.asList())
    }
}

dependencyResolutionManagement {

    // Centralized repository definitions
    repositories {
        mavenCentral()
    }

    // Catalogs
    versionCatalogs {
        create("libs") {
            // Kotlin & KotlinX
            val kotlinGroup = "org.jetbrains.kotlin"
            val kotlinxGroup = "org.jetbrains.kotlinx"
            val coroutinesPrefix = "kotlinx-coroutines"

            val kotlinVersion: String by settings
            val coroutinesVersion = "1.7.3"

            library("kotlin-stdlib-jdk8", kotlinGroup, "kotlin-stdlib-jdk8").version(kotlinVersion)
            library("kotlin-test-junit5", kotlinGroup, "kotlin-test-junit5").withoutVersion()
            library("$coroutinesPrefix-core", kotlinxGroup, "$coroutinesPrefix-core").version(coroutinesVersion)
            library("$coroutinesPrefix-jdk8", kotlinxGroup, "$coroutinesPrefix-jdk8").version(coroutinesVersion)
            library("$coroutinesPrefix-reactive", kotlinxGroup, "$coroutinesPrefix-reactive").version(coroutinesVersion)
            library("$coroutinesPrefix-reactor", kotlinxGroup, "$coroutinesPrefix-reactor").version(coroutinesVersion)
            library("$coroutinesPrefix-test", kotlinxGroup, "$coroutinesPrefix-test").version(coroutinesVersion)

            // Jooq
            val jooqVersion = "3.18.6"
            libsAndBundle(
                "org.jooq",
                jooqVersion,
                "jooq",
                "jooq-kotlin",
                "jooq-kotlin-coroutines",
                "jooq-postgres-extensions",
                bundleName = "jooq"
            )
            libsAndBundle("org.jooq", jooqVersion, "jooq-codegen")

            // Misc
            library("microutils-logging", "io.github.microutils", "kotlin-logging-jvm").version("3.0.5")
        }
    }
}

rootProject.name = "lovely-jooq"
