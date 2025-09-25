plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.lovely.gradle)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = "com.lovelysystems"

kotlin {
    jvmToolchain(21)
}

lovely {
    gitProject()
}

kover {
    reports {
        total {
            verify {
                onCheck = true
                rule {
                    bound {
                        minValue = 90
                        coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.INSTRUCTION
                    }
                }
            }
        }
    }
}

if (JavaVersion.current() != JavaVersion.VERSION_21) {
    // we require Java 21 here, to ensure we are always using the same version as the docker images are using
    error("Java 21 is required for this Project, found ${JavaVersion.current()}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.check {
    dependsOn("detekt")
    dependsOn("detektMain")
    dependsOn("detektTest")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.bundles.jooq)
    implementation(libs.spotbugs.annotations)
    implementation(libs.slf4j.api)

    testImplementation(testLibs.kotest.runner)
    testImplementation(testLibs.r2dbc.postgresql)
    testImplementation(testLibs.lovely.db.testing)
    testImplementation(testLibs.jooq.codegen)
    testImplementation(testLibs.logback)
}

dokka {
    val versionToUse = (project.findProperty("docVersion") as? String?) ?: project.version.toString()
    moduleVersion.set(versionToUse)
    dokkaSourceSets.main {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl("https://github.com/lovelysystems/lovely-jooq/tree/master/src")
            remoteLineSuffix.set("#L")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lovelysystems/lovely-jooq")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}
