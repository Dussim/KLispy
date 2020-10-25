import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "io.dussim"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/hotkeytlt/maven") }
}
dependencies {
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.0")
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:+")

    tasks.test {
        useJUnitPlatform()
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClassName = "MainKt"
}
