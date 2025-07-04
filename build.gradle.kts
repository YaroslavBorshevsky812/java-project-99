import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	application
	jacoco
	id("checkstyle")
	id("io.freefair.lombok") version "8.13.1"
	id("org.sonarqube") version "4.4.1.3373"
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

application { mainClass.set("hexlet.code.AppApplication") }

repositories {
	mavenCentral()
}

sonar {
	properties {
		property("sonar.projectKey", "YaroslavBorshevsky812_java-project-99")
		property("sonar.organization", "yaroslavborshevsky812")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

dependencies {
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql:42.1.4")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.5.0")
	testImplementation("org.springframework.security:spring-security-test:6.5.1")
	implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		showStackTraces = true
		showCauses = true
		showStandardStreams = true
	}
	finalizedBy(tasks.jacocoTestReport)
}


tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}
