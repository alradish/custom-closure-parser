plugins {
    kotlin("jvm") version "1.4.10"
}

group = "io.github.alrai"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(group= "org.mozilla", name= "rhino", version="1.7.13")
}