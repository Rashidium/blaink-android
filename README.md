# Blaink Android SDK

[![Build Status](https://github.com/blaink/blaink-android/workflows/Build%20and%20Test/badge.svg)](https://github.com/blaink/blaink-android/actions)
[![GitHub release](https://img.shields.io/github/release/blaink/blaink-android.svg)](https://github.com/blaink/blaink-android/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Android SDK for Blaink push notification and user management platform.

## Features

- 🔔 **Push Notifications**: FCM integration with delivery tracking
- 🔐 **SSL Pinning**: Enhanced security with certificate pinning
- 💾 **Secure Storage**: Encrypted data storage using Android Keystore
- 🔄 **Session Management**: Automatic token refresh and user session handling
- 📅 **Calendar Integration**: Schedule and manage calendar events
- 🛠 **Multi-module Architecture**: Modular design for better maintainability

## Installation

### GitHub Packages (Recommended)

Add the GitHub Packages repository to your project's `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/blaink/blaink-android")
        credentials {
            username = "your-github-username"
            password = "your-github-token"
        }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.blaink:blaink-android:1.0.0")
}
```

### JitPack (Alternative)

Add JitPack repository:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.blaink:blaink-android:1.0.0")
}
```

## Quick Start

### 1. Initialize Firebase

Add Firebase to your Android project by following the [official documentation](https://firebase.google.com/docs/android/setup).

### 2. Setup Blaink SDK

Initialize the SDK in your `Application` class:

```kotlin
class MyApplication : Application(), BlainkDelegate {
    
    override fun onCreate() {
        super.onCreate()
        
        val blaink = Blaink.getInstance()
        blaink.delegate = this
        blaink.setup(
            context = this,
            sdkKey = "your_sdk_key_here",
            environment = PushEnvironment.PRODUCTION,
            isDebugLogsEnabled = BuildConfig.DEBUG
        )
    }
    
    override fun didReceiveNotification(notification: Map<String, Any>) {
        // Handle notification received
        println("Notification received: $notification")
    }
    
    override fun didRegisterForBlainkNotifications(blainkUserId: String) {
        // Handle successful registration
        println("Registered with user ID: $blainkUserId")
    }
}
```

### 3. Register FCM Token

Register for push notifications in your main activity:

```kotlin
class MainActivity : AppCompatActivity() {
    
    private val blaink = Blaink.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get FCM token and register with Blaink
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                blaink.registerForRemoteNotifications(token)
            }
        }
    }
}
```

### 4. Update AndroidManifest.xml

Add the Blaink FCM service to your manifest:

```xml
<service
    android:name="com.blaink.push.BlainkFCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## Advanced Usage

### SSL Certificate Pinning

The SDK includes SSL certificate pinning for enhanced security. To update certificate hashes:

1. Update the hashes in `SSLPinningManager.kt`
2. Test with your server endpoints
3. Monitor certificate expiration dates

### Session Management

```kotlin
// Check authentication status
if (UserSession.isAuthenticated) {
    // User is logged in
}

// Logout user
lifecycleScope.launch {
    val result = blaink.logout()
    if (result.isSuccess) {
        // Logout successful
    }
}
```

### Notification Tracking

Track notification interactions:

```kotlin
// Track when notification is opened
blaink.trackNotificationAction(notificationId, "opened")

// Track when notification is dismissed
blaink.trackNotificationAction(notificationId, "dismissed")
```

## Configuration

### Environment Setup

Configure the SDK for different environments:

```kotlin
// Development environment
blaink.setup(
    context = this,
    sdkKey = "dev_sdk_key",
    environment = PushEnvironment.DEVELOPMENT,
    isDebugLogsEnabled = true
)

// Production environment
blaink.setup(
    context = this,
    sdkKey = "prod_sdk_key",
    environment = PushEnvironment.PRODUCTION,
    isDebugLogsEnabled = false
)
```

### ProGuard/R8

If you're using code obfuscation, add these rules to your `proguard-rules.pro`:

```proguard
# Blaink SDK
-keep class com.blaink.** { *; }
-dontwarn com.blaink.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
```

## Architecture

The SDK is built with a modular architecture:

- **blaink-core**: Core networking, storage, and SSL pinning
- **blaink-push**: FCM integration and notification handling
- **blaink**: Main SDK facade combining all modules

## Requirements

- Android API 21+ (Android 5.0)
- Kotlin 1.9.20+
- Firebase Cloud Messaging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:

- 📧 Email: dev@blaink.com
- 📖 Documentation: [https://docs.blaink.com](https://docs.blaink.com)
- 🐛 Issues: [GitHub Issues](https://github.com/blaink/blaink-android/issues)
