# Blaink Android SDK Integration Guide

## Project Structure

```
blaink-android/
├── blaink-core/              # Core networking, storage, SSL pinning
├── blaink-push/              # FCM integration and notifications
├── blaink/                   # Main SDK facade
├── sample/                   # Sample application
├── .github/workflows/        # CI/CD pipelines
└── gradle/                   # Gradle configuration
```

## Step-by-Step Integration

### 1. Prerequisites

- Android Studio Arctic Fox or later
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34 (Android 14)
- Firebase project with FCM enabled

### 2. Add Dependencies

#### Option A: GitHub Packages (Recommended for private repos)

```kotlin
// In your app-level build.gradle.kts
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/blaink/blaink-android")
        credentials {
            username = "your-github-username"
            password = "your-github-personal-access-token"
        }
    }
}

dependencies {
    implementation("com.blaink:blaink-android:1.0.0")
}
```

#### Option B: JitPack (For public repos)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.blaink:blaink-android:1.0.0")
}
```

### 3. Firebase Setup

1. Add your `google-services.json` file to your `app/` directory
2. Apply the Google Services plugin in your app-level `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

### 4. Initialize SDK

Create an Application class that implements `BlainkDelegate`:

```kotlin
import com.blaink.Blaink
import com.blaink.core.BlainkDelegate
import com.blaink.core.PushEnvironment

class MyApplication : Application(), BlainkDelegate {
    
    override fun onCreate() {
        super.onCreate()
        
        val blaink = Blaink.getInstance()
        blaink.delegate = this
        blaink.setup(
            context = this,
            sdkKey = "your_blaink_sdk_key", // Get this from Blaink dashboard
            environment = if (BuildConfig.DEBUG) {
                PushEnvironment.DEVELOPMENT
            } else {
                PushEnvironment.PRODUCTION
            },
            isDebugLogsEnabled = BuildConfig.DEBUG
        )
    }
    
    override fun didReceiveNotification(notification: Map<String, Any>) {
        // Handle notification received
        // This is called when a push notification is received
        Log.d("Blaink", "Notification received: $notification")
        
        // Extract notification data
        val title = notification["title"] as? String
        val body = notification["body"] as? String
        val customData = notification["customData"] as? Map<String, Any>
        
        // Handle notification display or navigation
    }
    
    override fun didRegisterForBlainkNotifications(blainkUserId: String) {
        // Handle successful registration
        Log.d("Blaink", "Registered with Blaink user ID: $blainkUserId")
        
        // Save user ID if needed for analytics or other purposes
        // This ID uniquely identifies this device/user in Blaink system
    }
}
```

### 5. Register Application Class

Update your `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ... >
```

### 6. Register FCM Token

In your main activity or where appropriate:

```kotlin
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    
    private val blaink = Blaink.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Register for FCM token
        setupPushNotifications()
    }
    
    private fun setupPushNotifications() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            
            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "FCM Registration Token: $token")
            
            // Register the token with Blaink
            blaink.registerForRemoteNotifications(token)
        }
    }
}
```

### 7. Add FCM Service to Manifest

Update your `AndroidManifest.xml` to include the Blaink FCM service:

```xml
<application>
    <!-- Your existing components -->
    
    <!-- Blaink FCM Service -->
    <service
        android:name="com.blaink.push.BlainkFCMService"
        android:exported="false">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>
</application>
```

### 8. Handle Notification Interactions

Track notification interactions for analytics:

```kotlin
// When user taps on notification
blaink.trackNotificationAction(notificationId, "opened")

// When user dismisses notification
blaink.trackNotificationAction(notificationId, "dismissed")

// When user performs custom action
blaink.trackNotificationAction(notificationId, "custom_action_name")
```

### 9. Advanced Features

#### Session Management

```kotlin
import com.blaink.core.storage.UserSession

// Check if user is authenticated
if (UserSession.isAuthenticated) {
    // User has valid session
    val accessToken = UserSession.accessToken
}

// Logout user
lifecycleScope.launch {
    val result = blaink.logout()
    if (result.isSuccess) {
        // Logout successful, redirect to login screen
    } else {
        // Handle logout error
        val error = result.exceptionOrNull()
    }
}
```

#### Error Handling

```kotlin
// Get current user information
lifecycleScope.launch {
    val result = blaink.getCurrentUser()
    result.onSuccess { userId ->
        // User information retrieved successfully
        Log.d("Blaink", "Current user ID: $userId")
    }.onFailure { exception ->
        // Handle error
        Log.e("Blaink", "Failed to get user info", exception)
    }
}
```

## Testing

### Development Environment

1. Use `PushEnvironment.DEVELOPMENT` for testing
2. Enable debug logs with `isDebugLogsEnabled = true`
3. Test with Firebase Test Lab or real devices

### Push Notification Testing

1. Use Firebase Console to send test notifications
2. Include `notificationID` in the payload for tracking
3. Verify notification delivery and interaction tracking

### SSL Pinning Testing

1. Test with Charles Proxy or similar tools
2. Verify certificate pinning blocks man-in-the-middle attacks
3. Test certificate rotation scenarios

## Troubleshooting

### Common Issues

1. **FCM token not received**
   - Check Firebase configuration
   - Verify `google-services.json` is in the correct location
   - Ensure Google Play Services are installed on device

2. **SSL pinning failures**
   - Verify certificate hashes are correct
   - Check domain configuration in `SSLPinningManager`
   - Test with actual server endpoints

3. **Authentication issues**
   - Verify SDK key is correct
   - Check network connectivity
   - Review server logs for authentication errors

### Debug Logs

Enable debug logging to see detailed SDK operations:

```kotlin
blaink.setup(
    context = this,
    sdkKey = "your_sdk_key",
    environment = PushEnvironment.DEVELOPMENT,
    isDebugLogsEnabled = true // Enable for debugging
)
```

Look for logs with the "Blaink" tag in Logcat.

## ProGuard/R8 Configuration

If using code shrinking, add these rules to `proguard-rules.pro`:

```proguard
# Blaink SDK
-keep class com.blaink.** { *; }
-dontwarn com.blaink.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.blaink.**$$serializer { *; }
-keepclassmembers class com.blaink.** {
    *** Companion;
}
-keepclasseswithmembers class com.blaink.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
```

## Migration from Other SDKs

### From Native Implementation

1. Remove existing push notification code
2. Replace with Blaink SDK initialization
3. Update notification handling logic
4. Test thoroughly with existing notification flows

### Version Updates

When updating the SDK version:

1. Check the changelog for breaking changes
2. Update certificate hashes if needed
3. Test all notification flows
4. Verify SSL pinning still works

## Security Best Practices

1. **API Keys**: Store SDK keys securely, never in version control
2. **Certificate Pinning**: Regularly update certificate hashes
3. **Secure Storage**: SDK uses Android Keystore for sensitive data
4. **Network Security**: All API calls use HTTPS with certificate pinning

## Performance Considerations

1. **Initialization**: SDK initializes asynchronously to avoid blocking main thread
2. **Storage**: Uses efficient encrypted storage for session data
3. **Network**: Implements connection pooling and request optimization
4. **Memory**: Designed to minimize memory footprint

## Support

For integration support:
- 📧 Email: dev@blaink.com
- 📖 Documentation: https://docs.blaink.com
- 🐛 Issues: GitHub Issues
