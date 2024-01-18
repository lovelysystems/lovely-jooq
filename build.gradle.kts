import java.net.URL

plugins {
    kotlin("jvm")
    id("com.lovelysystems.gradle")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lovelysystems"

kotlin {
    jvmToolchain(17)
}

lovely {
    gitProject()
}

koverReport {
    defaults {
        verify {
            onCheck = true
            rule {
                bound {
                    minValue = 90
                    metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION
                }
            }
        }
    }
}

if (JavaVersion.current() != JavaVersion.VERSION_17) {
    // we require Java 17 here, to ensure we are always using the same version as the docker images are using
    error("Java 17 is required for this Project, found ${JavaVersion.current()}")
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

tasks.dokkaHtml {
    val versionToUse = (project.findProperty("docVersion") as? String?) ?: project.version.toString()
    moduleVersion.set(versionToUse)
    dokkaSourceSets {
        named("main") {
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/lovelysystems/lovely-jooq/tree/master/src"))
                remoteLineSuffix.set("#L")
            }
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
