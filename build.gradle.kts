import kotlin.random.Random

plugins {
	java
	idea
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.avast.gradle.docker-compose") version "0.17.6"
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

idea {
	module {
		testSources.from(sourceSets["integration"].java.srcDirs)
	}
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
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	integrationImplementation("org.springframework.boot:spring-boot-starter-test")
	integrationImplementation("org.springframework.security:spring-security-test")
	integrationImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	integrationImplementation("org.springframework.boot:spring-boot-starter-webflux")
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
	}
}

dockerCompose.isRequiredBy(integrationTest)

dockerCompose {
	isRequiredBy(integrationTest)
	useDockerComposeV2 = true
	dockerExecutable = "/usr/local/bin/docker"
	waitForTcpPorts = false
}

tasks.check {
	dependsOn(tasks.test)
	dependsOn(integrationTest)
}
