plugins {
    `java-library`
    `maven-publish`
}

group = "com.sammwy.milkshake"
version = "2.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // MongoDB Java Driver
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")

    // SQLite Java Driver
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    // MySQL Java Driver
    implementation("mysql:mysql-connector-java:8.0.33")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
