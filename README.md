# Blaink Android SDK

[![Build Status](https://github.com/blaink/blaink-android/workflows/Build%20and%20Test/badge.svg)](https://github.com/blaink/blaink-android/actions)
[![GitHub release](https://img.shields.io/github/release/blaink/blaink-android.svg)](https://github.com/blaink/blaink-android/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Android SDK for Blaink push notification and user management platform.

## Features

- Push Notifications: FCM integration with automatic token management
- Automatic Deeplink Handling: No manual `onNewIntent` handling required
- SSL Pinning: Enhanced security with certificate pinning
- Secure Storage: Encrypted data storage using Android Keystore
- Session Management: Automatic token refresh and user session handling

## Installation

### GitHub Packages

Add the GitHub Packages repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/Rashidium/blaink-android")
            credentials {
                username = "your-github-username"
                password = "your-github-token" // with read:packages scope
            }
        }
    }
}
```

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.blaink:blaink:1.0.2")
}
```

### JitPack (Alternative)

Add JitPack repository to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.Rashidium:blaink-android:1.0.2")
}
```

## Quick Start

### 1. Add Firebase to Your Project

Follow the [Firebase Android setup guide](https://firebase.google.com/docs/android/setup) to add Firebase to your project. Make sure to:

1. Add `google-services.json` to your app module
2. Apply the Google Services plugin in your `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

### 2. Update AndroidManifest.xml

Add the Blaink FCM service and deeplink intent filter:

```xml
<application ...>

    <!-- Blaink FCM Service -->
    <service
        android:name="com.blaink.push.BlainkFCMService"
        android:exported="false">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>

    <!-- Add deeplink support to your launcher activity -->
    <activity
        android:name=".MainActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>

        <!-- Blaink Deeplink Handler -->
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="YOUR_DEEPLINK_SCHEME" />
        </intent-filter>
    </activity>

</application>
```

Replace `YOUR_DEEPLINK_SCHEME` with your app's deeplink scheme (provided by Blaink).

### 3. Initialize the SDK

Create or update your `Application` class:

```kotlin
class MyApplication : Application(), BlainkDelegate {

    override fun onCreate() {
        super.onCreate()

        Blaink.getInstance().apply {
            delegate = this@MyApplication
            setup(
                context = this@MyApplication,
                sdkKey = "YOUR_SDK_KEY",
                environment = PushEnvironment.PRODUCTION,
                isDebugLogsEnabled = BuildConfig.DEBUG
            )
        }
    }

    override fun didReceiveNotification(notification: Map<String, Any>) {
        // Called when a push notification is received
        Log.d("Blaink", "Notification received: $notification")
    }

    override fun didRegisterForBlainkNotifications(blainkUserId: String) {
        // Called when device is successfully registered with Blaink
        Log.d("Blaink", "Registered with user ID: $blainkUserId")
    }
}
```

Don't forget to register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
```

### 4. Register for Push Notifications

In your main activity, call `registerForRemoteNotifications()`:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register for push notifications
        // This automatically:
        // - Requests POST_NOTIFICATIONS permission on Android 13+
        // - Fetches the FCM token
        // - Registers with Blaink backend
        Blaink.getInstance().registerForRemoteNotifications(this)
    }
}
```

That's it! The SDK automatically handles:
- FCM token retrieval and registration
- Notification permission requests (Android 13+)
- Deeplink handling via `ActivityLifecycleCallbacks`

## Advanced Usage

### Manual FCM Token Registration

If you manage FCM tokens yourself, use `registerFCMToken()` instead:

```kotlin
FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
    Blaink.getInstance().registerFCMToken(token)
}
```

### Manual Deeplink Handling

The SDK automatically handles deeplinks, but you can also handle them manually:

```kotlin
// Returns true if the deeplink was handled by Blaink
val handled = Blaink.getInstance().handleDeeplink(uri.toString())
```

### Session Management

```kotlin
// Check if user is authenticated
if (UserSession.isAuthenticated) {
    // User is logged in
}

// Get current user
lifecycleScope.launch {
    val result = Blaink.getInstance().getCurrentUser()
    result.onSuccess { userId ->
        Log.d("Blaink", "User ID: $userId")
    }
}

// Logout
lifecycleScope.launch {
    val result = Blaink.getInstance().logout()
    result.onSuccess {
        Log.d("Blaink", "Logged out successfully")
    }
}
```

### Notification Tracking

Track notification interactions:

```kotlin
// Track when notification is opened
Blaink.getInstance().trackNotificationAction(notificationId, "opened")

// Track when notification is dismissed
Blaink.getInstance().trackNotificationAction(notificationId, "dismissed")
```

## Configuration

### Environment Setup

```kotlin
// Development (for testing)
Blaink.getInstance().setup(
    context = this,
    sdkKey = "YOUR_SDK_KEY",
    environment = PushEnvironment.DEVELOPMENT,
    isDebugLogsEnabled = true
)

// Production
Blaink.getInstance().setup(
    context = this,
    sdkKey = "YOUR_SDK_KEY",
    environment = PushEnvironment.PRODUCTION,
    isDebugLogsEnabled = false
)
```

### ProGuard / R8

Add these rules to your `proguard-rules.pro`:

```proguard
# Blaink SDK
-keep class com.blaink.** { *; }
-dontwarn com.blaink.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
```

## Requirements

- Android API 21+ (Android 5.0)
- Kotlin 1.9.20+
- Firebase Cloud Messaging

## Module Structure

The SDK is built with a modular architecture:

| Module | Description |
|--------|-------------|
| `blaink` | Main SDK facade - use this in your app |
| `blaink-core` | Core networking, storage, and SSL pinning |
| `blaink-push` | FCM integration and notification handling |

## Migration Guide

### From 1.0.x to 1.1.0

The SDK now handles FCM token retrieval automatically. Update your code:

**Before:**
```kotlin
// In MainActivity
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        Blaink.getInstance().registerForRemoteNotifications(task.result)
    }
}

// Handle deeplinks manually
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    intent.data?.let { Blaink.getInstance().handleDeeplink(it.toString()) }
}
```

**After:**
```kotlin
// In MainActivity - that's all you need!
Blaink.getInstance().registerForRemoteNotifications(this)

// Deeplinks are handled automatically - no onNewIntent override needed
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- Email: support@blaink.com
- Documentation: [https://docs.blaink.com](https://docs.blaink.com)
- Issues: [GitHub Issues](https://github.com/Rashidium/blaink-android/issues)
