plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.biomedix"
version = "2.4.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Spring Boot Core Web & Data
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebClient for external biological APIs

    // Kotlin Extensions & Coroutines for Concurrent Module Execution
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Database: PostgreSQL with JSONB Hypersistence Types
    runtimeOnly("org.postgresql:postgresql:42.7.2")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    // Bioinformatics: BioKt strictly required
    implementation("com.github.shankarnvs:biokt:v1.1.0")

    // Math for Structural & Genomic CNN Predictions
    implementation("org.apache.commons:commons-math3:3.6.1")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
