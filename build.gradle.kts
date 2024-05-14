import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import kotlin.random.Random

plugins {
    java
    idea
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.gorylenko.gradle-git-properties") version "1.5.1"
}

group = "com.sourceallies"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

sourceSets {
    create("integration") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val integrationRuntimeOnly by configurations.getting //{

configurations["integrationRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())
configurations["integrationCompileOnly"].extendsFrom(configurations.compileOnly.get())
configurations["integrationAnnotationProcessor"].extendsFrom(configurations.annotationProcessor.get())

idea {
    module {
        testSources.from(sourceSets["integration"].java.srcDirs)
    }
}

gitProperties {
    keys = listOf(
            "git.branch",
            "git.commit.id",
            "git.commit.time"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("org.springframework.security:spring-security-test")
    integrationImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
    }

    systemProperty("random_number_to_force_tests_to_always_run", Random.nextInt())
}

val integrationTest = task<Test>("integrationTest") {
    description = "Run integration tests"
    group = "verification"

    testClassesDirs = sourceSets["integration"].output.classesDirs
    classpath = sourceSets["integration"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.check {
    dependsOn(tasks.test)
    dependsOn(integrationTest)
}
