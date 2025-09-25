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
        create("testLibs") {
            from(files("gradle/testLibs.versions.toml"))
        }
    }
}

rootProject.name = "lovely-jooq"
