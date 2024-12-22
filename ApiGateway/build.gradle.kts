plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.sidalitech"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot and WebFlux dependencies
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")  // For WebFlux support
//	implementation("org.springframework.security:spring-security-webflux")

	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// JSON and Kotlin dependencies
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")  // For reactive coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	// JWT dependencies
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")  // JWT API
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")  // JWT Implementation
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")  // JSON serialization/deserialization for JWT

	// Spring Cloud dependencies (if needed, uncomment if used)
	 implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	// implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2024.0.0"))

	// Runtime only dependencies for testing
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Security WebFlux

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
