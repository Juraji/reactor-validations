import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("maven")
}

group = "nl.juraji"
version = "1.0.5"
java.sourceCompatibility = JavaVersion.VERSION_14
java.targetCompatibility = JavaVersion.VERSION_14

repositories {
    mavenCentral()
}

dependencies {
    val projectReactor = "3.4.0"
    val junit = "5.7.0"

    implementation("io.projectreactor:reactor-core:$projectReactor")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit")
    testImplementation("io.projectreactor:reactor-test:$projectReactor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = java.targetCompatibility.majorVersion
    }
}

tasks.register("version") {
    println(version)
}
