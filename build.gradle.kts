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
}

allprojects {
    group = "com.blaink"
    version = "1.0.0"
}

// Publishing configuration will be set up later
//subprojects {
//    apply(plugin = "maven-publish")
//    apply(plugin = "signing")
//    
//    publishing {
//        repositories {
//            maven {
//                name = "GitHubPackages"
//                url = uri("https://maven.pkg.github.com/blaink/blaink-android")
//                credentials {
//                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
//                }
//            }
//        }
//    }
//}

tasks.register("cleanAll", Delete::class) {
    delete(layout.buildDirectory)
}
