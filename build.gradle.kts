plugins {
    kotlin("jvm")
    id("com.lovelysystems.gradle")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}

group = "com.lovelysystems"

lovely {
    gitProject()
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
