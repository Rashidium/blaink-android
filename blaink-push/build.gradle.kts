plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.blaink.push"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }


    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(project(":blaink-core"))
    
    // Firebase BOM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    
    // Network dependencies (needed for API calls)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    
    // Testing
    testImplementation(libs.junit)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.blaink"
            artifactId = project.name
            version = project.version.toString()
            
            afterEvaluate {
                from(components["release"])
            }
            
            pom {
                name.set("Blaink Android SDK - ${project.name}")
                description.set("Android SDK for Blaink push notification and messaging platform")
                url.set("https://github.com/Rashidium/blaink-android")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("rashidium")
                        name.set("Rashid Ramazanov")
                        email.set("support@blaink.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:github.com/Rashidium/blaink-android.git")
                    developerConnection.set("scm:git:ssh://github.com/Rashidium/blaink-android.git")
                    url.set("https://github.com/Rashidium/blaink-android/tree/main")
                }
            }
        }
    }
}