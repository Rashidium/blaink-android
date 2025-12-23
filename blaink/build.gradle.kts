import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish.central)
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

    // Firebase (for automatic FCM token retrieval)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary(
        variant = "release",
        sourcesJar = true,
        publishJavadocJar = true
    ))

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = "com.blainks",
        artifactId = "blaink",
        version = project.version.toString()
    )

    pom {
        name.set("Blaink Android SDK")
        description.set("Android SDK for Blaink push notification and messaging platform")
        url.set("https://github.com/Rashidium/blaink-android")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
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
