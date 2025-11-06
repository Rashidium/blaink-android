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
    id("maven-publish")
    id("signing")
}

allprojects {
    group = "com.blaink"
    version = System.getenv("RELEASE_VERSION") ?: "1.0.2"
}

subprojects {
    if (name in listOf("blaink", "blaink-core", "blaink-push")) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/Rashidium/blaink-android")
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                    }
                }
            }
        }
    }
}

tasks.register("cleanAll", Delete::class) {
    delete(layout.buildDirectory)
}