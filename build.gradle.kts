plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "nl.avans"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}

application {
    mainClassName = "nl.avans.wordcrex.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.zaxxer:HikariCP:3.3.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.4.1")
    implementation("org.slf4j:slf4j-simple:1.7.26")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}
