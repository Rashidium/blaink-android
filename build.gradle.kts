buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.maven.publish.central) apply false
}

allprojects {
    group = "com.blainks"
    version = System.getenv("RELEASE_VERSION") ?: "1.3.46"
}

tasks.register("cleanAll", Delete::class) {
    delete(layout.buildDirectory)
}
