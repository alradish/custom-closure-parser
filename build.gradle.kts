plugins {
    kotlin("jvm") version "1.4.10"
}

group = "io.github.alrai"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/hotkeytlt/maven") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.0")
}
