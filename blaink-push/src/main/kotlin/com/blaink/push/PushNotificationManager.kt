//
//  PushNotificationManager.kt
//  Blaink
//

package com.blaink.push

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
        Logger.d("Token refresh handled: $token")
    }

    /**
     * Handle received remote message
     */
    fun handleRemoteMessage(message: RemoteMessage) {
        Logger.d("Handling remote message: ${message.data}")

        // Convert to map for delegate callback
        val notificationData = message.data.toMap()
        delegate?.didReceiveNotification(notificationData)
    }

    /**
     * Track notification action (delivered, opened, dismiss)
     */
    fun trackNotificationAction(notificationId: String, action: String) {
        Logger.d("Tracking notification action: $notificationId -> $action")

        scope.launch {
            try {
                val request = ApnsNotificationRequest(
                    id = notificationId,
                    action = action
                )
                BlainkApiClient.apnsApi.updateNotification(request)
                Logger.d("Successfully tracked: $notificationId -> $action")
            } catch (e: Exception) {
                Logger.e("Failed to track notification action: $action", e)
            }
        }
    }

    /**
     * Handle notification open action
     */
    fun handleNotificationOpen(notificationData: Map<String, Any?>) {
        Logger.d("Handling notification open: $notificationData")

        // Filter out null values and notify delegate about notification open
        val filteredData = notificationData.filterValues { it != null }.mapValues { it.value!! }
        delegate?.didReceiveNotification(filteredData)
    }

    /**
     * Handle notification dismiss action
     */
    fun handleNotificationDismiss(notificationData: Map<String, Any?>) {
        Logger.d("Handling notification dismiss: $notificationData")

        // Filter out null values and notify delegate about notification dismiss
        val filteredData = notificationData.filterValues { it != null }.mapValues { it.value!! }
        delegate?.didReceiveNotification(filteredData)
    }
}
