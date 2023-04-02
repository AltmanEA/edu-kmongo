import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "ru.altmanea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.litote.kmongo:kmongo-serialization:4.8.0")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.8.0")
    implementation("org.json:json:20230227")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")
//    implementation("org.litote.kmongo:kmongo-async:4.2.4")
//    implementation("org.litote.kmongo:kmongo-coroutine:4.2.4")
//    implementation("org.litote.kmongo:kmongo-reactor:4.2.4")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}
