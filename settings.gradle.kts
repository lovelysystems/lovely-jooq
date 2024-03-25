pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion

        id("com.lovelysystems.gradle") version "1.12.0"
        id("io.gitlab.arturbosch.detekt") version "1.23.6"
        id("org.jetbrains.kotlinx.kover") version "0.7.5"
        id("org.jetbrains.dokka") version "1.9.20"
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

        // Our own source of non-published Gradle plugins (e.g. lovely-db-testing)
        maven {
            url = uri("https://raw.github.com/lovelysystems/maven/master/releases")
            content {
                includeGroup("com.lovelysystems")
            }
        }
    }

    // Catalogs
    versionCatalogs {
        val jooqVersion = "3.19.6"

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
            libsAndBundle(
                "org.jooq",
                jooqVersion,
                "jooq",
                "jooq-kotlin",
                "jooq-kotlin-coroutines",
                "jooq-postgres-extensions",
                bundleName = "jooq"
            )

            // Misc
            library("spotbugs-annotations", "com.github.spotbugs", "spotbugs-annotations").version("4.8.3")
            library("slf4j-api", "org.slf4j", "slf4j-api").version("2.0.11")
        }

        create("testLibs") {
            val kotestVersion = "5.8.1"

            library("kotest-runner", "io.kotest", "kotest-runner-junit5").version(kotestVersion)

            library("lovely-db-testing", "com.lovelysystems", "lovely-db-testing").version("0.2.0")
            library("r2dbc-postgresql", "org.postgresql", "r2dbc-postgresql").version("1.0.4.RELEASE")
            library("jooq-codegen", "org.jooq", "jooq-codegen").version(jooqVersion)

            library("logback", "ch.qos.logback", "logback-classic").version("1.5.3")
        }
    }
}

rootProject.name = "lovely-jooq"
