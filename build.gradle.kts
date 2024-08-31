plugins {
    id("java")
}

group = "org.sa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":util"))
}

tasks.test {
    useJUnitPlatform()
}