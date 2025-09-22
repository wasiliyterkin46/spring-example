plugins {
    application
    id("java")
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "8.14.2"
    id("com.github.ben-manes.versions") version "0.52.0"
}

group = "io.hexlet"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("io.hexlet.spring.HomeController")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("net.datafaker:datafaker:2.5.0")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    /*Важно!
    * Указание версии persistence-api:3.2.0 вызывает ошибку при запуске приложения.
    * Ошибка несовместимости Spring Data JPA / Hibernate / Jakarta.
    * Версия подключенного плагина Spring Boot на момент возникновения ошибки:
    * id("org.springframework.boot") version "3.5.5"
    * */

    runtimeOnly("com.h2database:h2:2.3.232")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
