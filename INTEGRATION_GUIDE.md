# Blaink Android SDK Integration Guide

## Quick Start

### 1. Add Repository

#### Option A: GitHub Packages (Recommended)
```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Rashidium/blaink-android")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}
```

#### Option B: JitPack (Easier for public use)
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

### 2. Add Dependencies

#### GitHub Packages
```gradle
dependencies {
    implementation "com.blaink:blaink:1.0.0"
}
```

#### JitPack
```gradle
dependencies {
    implementation 'com.github.Rashidium:blaink-android:v1.0.0'
}
```

### 3. Initialize SDK

```kotlin
import com.blaink.Blaink
import com.blaink.core.PushEnvironment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Blaink SDK
        Blaink.setup(
            context = applicationContext,
            sdkKey = "YOUR_SDK_KEY",
            environment = PushEnvironment.DEVELOPMENT,
            isDebugLogsEnabled = true
        )
    }
}
```

### 4. Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app with package name
3. Download `google-services.json`
4. Place it in your app's `src/main/` directory
5. Add Firebase plugin to your app's `build.gradle`:

```gradle
plugins {
    id 'com.google.gms.google-services'
}
```

### 5. Handle Push Notifications

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Set delegate for push notifications
        Blaink.delegate = object : BlainkDelegate {
            override fun didReceiveNotification(payload: Map<String, String>) {
                // Handle incoming push notification
                Log.d("Blaink", "Received notification: $payload")
            }
            
            override fun didRegisterForBlainkNotifications(userId: String) {
                // Called when device is registered with Blaink backend
                Log.d("Blaink", "Registered with user ID: $userId")
            }
        }
    }
}
```

## Project Structure

```
blaink-android/
├── blaink-core/              # Core networking, storage, SSL pinning
├── blaink-push/              # FCM integration and notifications
├── blaink/                   # Main SDK facade
├── sample/                   # Sample application
└── .github/workflows/        # CI/CD pipelines
```

## SDK Modules

### Core Module (`blaink-core`)
- **Networking**: HTTP client with SSL pinning
- **Storage**: Secure storage using AndroidX Security
- **Authentication**: Basic auth and token management
- **Device Info**: Device identification and metadata

### Push Module (`blaink-push`)
- **FCM Integration**: Firebase Cloud Messaging
- **Notification Handling**: Background and foreground notifications
- **Token Management**: Automatic token refresh and submission

### Main Module (`blaink`)
- **Public API**: Main entry point for SDK
- **Configuration**: Setup and initialization
- **Delegates**: Callback handling for events

## API Reference

### Blaink.setup()
Initialize the Blaink SDK with your configuration.

**Parameters:**
- `context`: Application context
- `sdkKey`: Your Blaink SDK key
- `environment`: PushEnvironment.DEVELOPMENT or PushEnvironment.PRODUCTION
- `isDebugLogsEnabled`: Enable debug logging

### Blaink.registerForRemoteNotifications()
Register for push notifications with FCM token.

**Parameters:**
- `deviceToken`: FCM registration token

### BlainkDelegate
Interface for handling SDK events.

**Methods:**
- `didReceiveNotification(payload: Map<String, String>)`: Called when push notification is received
- `didRegisterForBlainkNotifications(userId: String)`: Called when device is registered

## Security Features

### SSL Certificate Pinning
The SDK implements SSL certificate pinning for secure communication with Blaink servers.

### Secure Storage
Sensitive data (tokens, device IDs) are stored using AndroidX Security Crypto with hardware-backed encryption when available.

## Troubleshooting

### Common Issues

1. **FCM Token Registration Failed**
   - Ensure `google-services.json` is properly configured
   - Check that Google Play Services are available on the device
   - Verify Firebase project configuration

2. **SSL Pinning Failures**
   - Check device network connectivity
   - Ensure certificate hasn't expired
   - Verify server certificate matches pinned certificates

3. **Build Errors**
   - Ensure all required dependencies are included
   - Check that Firebase plugin is applied
   - Verify repository configuration

### Debug Logging

Enable debug logging to troubleshoot issues:

```kotlin
Blaink.setup(
    context = applicationContext,
    sdkKey = "YOUR_SDK_KEY",
    environment = PushEnvironment.DEVELOPMENT,
    isDebugLogsEnabled = true  // Enable debug logs
)
```

## Support

For issues and questions:
- GitHub Issues: [https://github.com/Rashidium/blaink-android/issues](https://github.com/Rashidium/blaink-android/issues)
- Email: support@blaink.com

## License

MIT License - see [LICENSE](LICENSE) file for details.