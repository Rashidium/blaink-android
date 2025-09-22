//
//  BlainkFCMService.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.push

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
}
