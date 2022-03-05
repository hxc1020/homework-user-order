import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
}

group = "com.thoughtworks"
version = "5.0.2"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("mysql:mysql-connector-java")
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("com.ninja-squad:springmockk:3.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("io.kotest:kotest-runner-junit5:$version")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.1.0")
    testImplementation ("io.kotest.extensions:kotest-extensions-mockserver:1.0.1")
    testImplementation("org.testcontainers:testcontainers:1.16.2")
    testImplementation("org.testcontainers:mysql:1.16.2")
    testImplementation("org.awaitility:awaitility:4.1.1")
    testImplementation("org.awaitility:awaitility-kotlin:4.1.1")
    implementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
