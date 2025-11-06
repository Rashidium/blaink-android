plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.blaink"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        buildConfigField("String", "SDK_VERSION", "\"${project.version}\"")
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
    
    buildFeatures {
        buildConfig = true
    }
    
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // Internal modules
    api(project(":blaink-core"))
    api(project(":blaink-push"))
    
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlinx.coroutines.android)
    
    // Network dependencies (for Retrofit Response classes)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
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
                url.set("https://github.com/Rashidium/blainks-ios")
                
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
                    connection.set("scm:git:github.com/Rashidium/blainks-ios.git")
                    developerConnection.set("scm:git:ssh://github.com/Rashidium/blainks-ios.git")
                    url.set("https://github.com/Rashidium/blainks-ios/tree/main")
                }
            }
        }
    }
}