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
    group = "io.github.rashidium"
    version = System.getenv("RELEASE_VERSION") ?: "1.3.46"
}

subprojects {
    if (name in listOf("blaink", "blaink-core", "blaink-push")) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")

        // Configure signing for Maven Central
        afterEvaluate {
            signing {
                val signingKey = System.getenv("GPG_PRIVATE_KEY")
                val signingPassword = System.getenv("GPG_PASSPHRASE")
                if (signingKey != null && signingPassword != null) {
                    useInMemoryPgpKeys(signingKey, signingPassword)
                    sign(publishing.publications)
                }
            }
        }

        publishing {
            repositories {
                // Maven Central (Sonatype)
                maven {
                    name = "MavenCentral"
                    url = uri("https://central.sonatype.com/api/v1/publisher/upload")
                    credentials {
                        username = System.getenv("MAVEN_CENTRAL_USERNAME") ?: ""
                        password = System.getenv("MAVEN_CENTRAL_PASSWORD") ?: ""
                    }
                }
                // GitHub Packages (keep as backup)
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