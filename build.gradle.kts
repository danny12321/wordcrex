plugins {
    application
}

group = "nl.avans"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}
