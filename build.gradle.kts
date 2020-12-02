import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("maven")
    id("maven-publish")
}

group = "nl.juraji"
version = "1.0.4"
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Reactor Validations")
                description.set("A Kotlin based validations api with Project Reactor extensions")
                url.set("https://github.com/Juraji/reactor-validations")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://github.com/Juraji/reactor-validations/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("juraji")
                        name.set("Juraji")
                        email.set("github@juraji.nl")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Juraji/reactor-validations.git")
                    developerConnection.set("scm:git:ssh://github.com/Juraji/reactor-validations.git")
                }
            }
        }
    }
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
