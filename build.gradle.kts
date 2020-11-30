import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
}

group = "nl.juraji"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_14
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    val projectReactor = "3.4.0"
    val junit = "5.7.0"
    val mockk = "1.10.2"

//    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
    implementation("io.projectreactor:reactor-core:$projectReactor")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit")
    testImplementation("io.projectreactor:reactor-test:$projectReactor")
    testImplementation("io.mockk:mockk:$mockk")
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
