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
