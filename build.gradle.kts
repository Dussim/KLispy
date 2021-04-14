import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-RC"
    application
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
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
    kotlinOptions.languageVersion = "1.5"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

application {
    mainClassName = "MainKt"
}
