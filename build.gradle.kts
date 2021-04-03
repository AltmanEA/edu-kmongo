import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "ru.altmanea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.litote.kmongo:kmongo-serialization:4.2.4")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.2.4")
    implementation("org.json:json:20201115")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.slf4j:slf4j-log4j12:1.7.25")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}