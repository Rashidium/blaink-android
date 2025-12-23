//
//  BlainkFCMService.kt
//  Blaink
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
        Logger.d("New FCM token received: $token")

        // Store the token
        SecureStorage.setPushNotificationToken(token)

        // Notify the push manager
        PushNotificationManager.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("Push notification received: ${message.data}")

        val notificationId = message.data["notificationID"]
        if (notificationId != null) {
            // Track delivery
            PushNotificationManager.trackNotificationAction(notificationId, "delivered")
        }

        // Build and display notification with open/dismiss tracking
        BlainkNotificationBuilder.showNotification(this, message)

        // Notify delegate about received notification
        PushNotificationManager.handleRemoteMessage(message)
    }
}
