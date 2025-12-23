//
//  BlainkFCMService.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.push

import android.content.Intent
import com.blaink.core.storage.SecureStorage
import com.blaink.core.utils.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging service for handling push notifications
 */
class BlainkFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("🔔 New FCM token received: $token")

        // Store the token
        SecureStorage.setPushNotificationToken(token)

        // Notify the push manager
        PushNotificationManager.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("🔔 Push notification received: ${message.data}")

        val notificationId = message.data["notificationID"]
        if (notificationId != null) {
            // Track delivery
            PushNotificationManager.trackNotificationAction(notificationId, "delivered")
        }

        // Handle the notification
        PushNotificationManager.handleRemoteMessage(message)
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
        Logger.d("🔔 Handling intent: ${intent.action} with extras: ${intent.extras}")

        val action = intent.action
        val notificationId = intent.getStringExtra("notificationID")

        when (action) {
            "com.google.android.c2dm.intent.RECEIVE" -> {
                // This is a standard FCM notification
                Logger.d("🔔 FCM notification received: $notificationId")

                // Check if this is a notification open (user tapped on notification)
                // This typically happens when the app is launched from a notification
                val isNotificationOpen = intent.getBooleanExtra("notification_open", false) ||
                                       intent.hasExtra("deeplink") ||
                                       intent.hasExtra("content")

                if (isNotificationOpen) {
                    Logger.d("🔔 Notification opened: $notificationId")
                    if (notificationId != null) {
                        // Track open action
                        PushNotificationManager.trackNotificationAction(notificationId, "open")
                    }

                    // Extract notification data and notify delegate
                    val notificationData = intent.extras?.let { extras ->
                        extras.keySet().associateWith { key -> extras.get(key) }
                    } ?: emptyMap()

                    PushNotificationManager.handleNotificationOpen(notificationData)
                } else {
                    // This is just a notification delivery, not an open action
                    Logger.d("🔔 Notification delivered (not opened): $notificationId")
                }
            }
        }
    }
}
