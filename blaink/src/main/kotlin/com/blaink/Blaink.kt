//
//  Blaink.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink

import android.content.Context
import com.blaink.core.BlainkDelegate
import com.blaink.core.PushEnvironment
import com.blaink.core.api.BlainkApiClient
import com.blaink.core.api.models.requests.ClientRequest
import com.blaink.core.api.models.requests.UpdateUserRequest
import com.blaink.core.api.ssl.SSLPinningManager
import com.blaink.core.storage.SecureStorage
import com.blaink.core.storage.UserSession
import com.blaink.core.utils.DeviceInfo
import com.blaink.core.utils.Logger
import com.blaink.push.PushNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Main Blaink SDK class for Android
 * 
 * Usage:
 * ```
 * val blaink = Blaink.getInstance()
 * blaink.delegate = this
 * blaink.setup(
 *     context = this,
 *     sdkKey = "your_sdk_key",
 *     environment = PushEnvironment.DEVELOPMENT,
 *     isDebugLogsEnabled = true
 * )
 * ```
 */
class Blaink private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: Blaink? = null
        
        /**
         * Get singleton instance of Blaink SDK
         */
        fun getInstance(): Blaink {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Blaink().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Delegate for SDK callbacks
     */
    var delegate: BlainkDelegate? = null
        set(value) {
            field = value
            PushNotificationManager.setDelegate(value)
        }
    
    private var sdkKey: String = ""
    private var environment: PushEnvironment = PushEnvironment.PRODUCTION
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    /**
     * Setup the Blaink SDK
     * 
     * @param context Application context
     * @param sdkKey Your Blaink SDK key
     * @param environment Push notification environment (development/production)
     * @param isDebugLogsEnabled Enable debug logging
     */
    fun setup(
        context: Context,
        sdkKey: String,
        environment: PushEnvironment = PushEnvironment.PRODUCTION,
        isDebugLogsEnabled: Boolean = false
    ) {
        this.sdkKey = sdkKey
        this.environment = environment
        
        // Initialize logging
        Logger.isDebugEnabled = isDebugLogsEnabled
        Logger.i("🚀 Initializing Blaink SDK v${BuildConfig.SDK_VERSION}")
        
        // Initialize storage
        SecureStorage.initialize(context)
        Logger.d("💾 Secure storage initialized")
        
        // Initialize SSL pinning
        SSLPinningManager.initialize(context, isDebugLogsEnabled)
        Logger.d("🔐 SSL pinning initialized")
        
        // Register device
        registerDevice(context)
    }
    
    /**
     * Register device with Blaink backend
     */
    private fun registerDevice(context: Context) {
        scope.launch {
            try {
                val deviceInfo = DeviceInfo.gather(context, environment)
                val request = ClientRequest(
                    clientId = SecureStorage.getBlainkClientId(),
                    sdkKey = sdkKey,
                    device = deviceInfo
                )
                
                Logger.d("📱 Registering device with Blaink...")
                val response = BlainkApiClient.authApi.initSdk(request)
                
                if (response.isSuccessful) {
                    val clientResponse = response.body()
                    if (clientResponse != null && !clientResponse.error) {
                        UserSession.accessToken = clientResponse.body.accessToken
                        UserSession.refreshToken = clientResponse.body.refreshToken
                        
                        Logger.i("✅ Device registered successfully with user ID: ${clientResponse.body.id}")
                        delegate?.didRegisterForBlainkNotifications(clientResponse.body.id)
                        
                        // Submit FCM token if available
                        val fcmToken = SecureStorage.getPushNotificationToken()
                        if (fcmToken != null) {
                            submitFCMToken(fcmToken)
                        }
                    }
                } else {
                    Logger.e("❌ Device registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.e("❌ Device registration failed", e)
            }
        }
    }
    
    /**
     * Register for remote notifications with FCM token
     * 
     * @param deviceToken FCM registration token
     */
    fun registerForRemoteNotifications(deviceToken: String) {
        Logger.d("🔔 Registering FCM token: $deviceToken")
        
        // Store the token
        SecureStorage.setPushNotificationToken(deviceToken)
        
        // Submit to backend if authenticated
        if (UserSession.isAuthenticated) {
            submitFCMToken(deviceToken)
        }
    }
    
    /**
     * Submit FCM token to backend
     */
    private fun submitFCMToken(token: String) {
        scope.launch {
            try {
                val request = UpdateUserRequest(
                    pushNotificationToken = token,
                    pushEnvironment = environment
                )
                
                val response = BlainkApiClient.authApi.updateUser(request)
                if (response.isSuccessful) {
                    Logger.d("✅ FCM token submitted successfully")
                } else {
                    Logger.e("❌ Failed to submit FCM token: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.e("❌ Failed to submit FCM token", e)
            }
        }
    }
    
    /**
     * Handle notification received callback
     * 
     * @param notificationData Notification payload
     */
    fun didReceiveNotification(notificationData: Map<String, Any>) {
        Logger.d("🔔 Notification received: $notificationData")
        
        val notificationId = notificationData["notificationID"] as? String
        if (notificationId != null) {
            PushNotificationManager.trackNotificationAction(notificationId, "delivered")
        }
        
        delegate?.didReceiveNotification(notificationData)
    }
    
    /**
     * Track notification action (opened, dismissed, etc.)
     * 
     * @param notificationId Notification ID
     * @param action Action performed (opened, dismissed, etc.)
     */
    fun trackNotificationAction(notificationId: String, action: String) {
        PushNotificationManager.trackNotificationAction(notificationId, action)
    }
    
    /**
     * Get current user information
     */
    suspend fun getCurrentUser(): Result<String?> {
        return try {
            if (!UserSession.isAuthenticated) {
                Result.failure(Exception("User not authenticated"))
            } else {
                val response = BlainkApiClient.authApi.getCurrentUser()
                if (response.isSuccessful) {
                    Result.success(response.body()?.body?.id)
                } else {
                    Result.failure(Exception("Failed to get user: ${response.errorBody()?.string()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Logout user and clear session
     */
    suspend fun logout(): Result<Unit> {
        return try {
            if (UserSession.isAuthenticated) {
                val response = BlainkApiClient.authApi.logout()
                if (response.isSuccessful) {
                    UserSession.clear()
                    SecureStorage.setPushNotificationToken(null)
                    Logger.i("👋 User logged out successfully")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Logout failed: ${response.errorBody()?.string()}"))
                }
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            UserSession.clear() // Clear local session anyway
            Result.failure(e)
        }
    }
    
    /**
     * Handle deeplink from {scheme}://blainks.com/{UDID} format
     * 
     * @param uri The deeplink URI to handle
     * @return true if the deeplink was handled successfully, false otherwise
     */
    fun handleDeeplink(uri: android.net.Uri): Boolean {
        // Check if host is blainks.com
        if (uri.host != "blainks.com") {
            Logger.w("⚠️ Invalid deeplink host: ${uri.host}")
            return false
        }
        
        // Extract UDID from path
        val udid = uri.path?.trimStart('/')?.takeIf { it.isNotEmpty() }
        
        if (udid.isNullOrEmpty()) {
            Logger.w("⚠️ Empty UDID in deeplink")
            return false
        }
        
        Logger.d("🔗 Handling deeplink with UDID: $udid")
        
        // Call API
        scope.launch {
            try {
                val request = com.blaink.core.api.models.requests.TestDeviceRegisterRequest(udid = udid)
                val response = BlainkApiClient.testDeviceApi.register(request)
                
                if (response.isSuccessful) {
                    Logger.i("✅ Test device registered successfully")
                } else {
                    Logger.e("❌ Failed to register test device: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.e("❌ Failed to register test device", e)
            }
        }
        
        return true
    }
}
