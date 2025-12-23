//
//  PushNotificationManager.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.push

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blaink.core.BlainkDelegate
import com.blaink.core.api.BlainkApiClient
import com.blaink.core.api.models.requests.ApnsNotificationRequest
import com.blaink.core.utils.Logger
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manages push notification operations
 */
object PushNotificationManager {
    private var delegate: BlainkDelegate? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Set the delegate for push notification callbacks
     */
    fun setDelegate(delegate: BlainkDelegate?) {
        this.delegate = delegate
    }
    
    /**
     * Handle FCM token refresh
     */
    fun onTokenRefresh(token: String) {
        // This will be handled by the main Blaink SDK
        // when it calls registerForRemoteNotifications
        Logger.d("🔔 Token refresh handled: $token")
    }
    
    /**
     * Handle received remote message
     */
    fun handleRemoteMessage(message: RemoteMessage) {
        Logger.d("🔔 Handling remote message: ${message.data}")
        
        // Convert to map for delegate callback
        val notificationData = message.data.toMap()
        delegate?.didReceiveNotification(notificationData)
    }
    
    /**
     * Track notification action (delivered, opened, dismissed)
     */
    fun trackNotificationAction(notificationId: String, action: String) {
        Logger.d("🔔 Tracking notification action: $notificationId -> $action")
        
        scope.launch {
            try {
                val request = ApnsNotificationRequest(
                    id = notificationId,
                    action = action
                )
                BlainkApiClient.apnsApi.updateNotification(request)
            } catch (e: Exception) {
                Logger.e("❌ Failed to track notification action", e)
            }
        }
    }
    
    /**
     * Handle notification open action
     */
    fun handleNotificationOpen(notificationData: Map<String, Any?>) {
        Logger.d("🔔 Handling notification open: $notificationData")
        
        // Filter out null values and notify delegate about notification open
        val filteredData = notificationData.filterValues { it != null } as Map<String, Any>
        delegate?.didReceiveNotification(filteredData)
    }
    
    /**
     * Handle notification dismiss action
     */
    fun handleNotificationDismiss(notificationData: Map<String, Any?>) {
        Logger.d("🔔 Handling notification dismiss: $notificationData")
        
        // Filter out null values and notify delegate about notification dismiss
        val filteredData = notificationData.filterValues { it != null } as Map<String, Any>
        delegate?.didReceiveNotification(filteredData)
    }
}

class NotificationDismissService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getStringExtra("notificationID")
        if (id != null) {
            Logger.d("🔔 Notification DISMISSED: $id")
            PushNotificationManager.trackNotificationAction(id, "dismiss")
        }
        stopSelf(startId)
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}