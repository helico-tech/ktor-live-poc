plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.ktor)
}

group = "nl.helicotech"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}


dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.templating.html)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.websockets)

    implementation(libs.cashapp.molecule.runtime)

    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}