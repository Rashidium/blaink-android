//
//  BlainkNotificationBuilder.kt
//  Blaink
//

package com.blaink.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.blaink.core.utils.Logger
import com.google.firebase.messaging.RemoteMessage

/**
 * Builds and displays notifications with proper open/dismiss tracking
 */
object BlainkNotificationBuilder {

    private const val CHANNEL_ID = "blaink_notifications"
    private const val CHANNEL_NAME = "Blaink Notifications"

    /**
     * Build and display a notification from a RemoteMessage
     */
    fun showNotification(context: Context, message: RemoteMessage) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O+
        createNotificationChannel(notificationManager)

        // Extract data
        val data = message.data
        val notificationId = data["notificationID"] ?: return
        val title = message.notification?.title ?: data["title"] ?: "Notification"
        val body = message.notification?.body ?: data["body"] ?: ""
        val deeplink = data["deeplink"]

        // Generate unique notification ID for Android
        val androidNotificationId = notificationId.hashCode()

        // Create open intent
        val openIntent = createOpenIntent(context, notificationId, deeplink, data, androidNotificationId)

        // Create dismiss intent
        val dismissIntent = createDismissIntent(context, notificationId, androidNotificationId)

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(getSmallIcon(context))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openIntent)
            .setDeleteIntent(dismissIntent)

        // Show the notification
        notificationManager.notify(androidNotificationId, builder.build())
        Logger.d("Notification displayed: $notificationId (android id: $androidNotificationId)")
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Push notifications from Blaink"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createOpenIntent(
        context: Context,
        notificationId: String,
        deeplink: String?,
        data: Map<String, String>,
        androidNotificationId: Int
    ): PendingIntent {
        val intent = Intent(context, NotificationOpenReceiver::class.java).apply {
            action = "com.blaink.NOTIFICATION_OPENED"
            putExtra("notificationID", notificationId)
            putExtra("deeplink", deeplink)
            putExtra("androidNotificationId", androidNotificationId)
            // Pass all data as extras
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        return PendingIntent.getBroadcast(
            context,
            androidNotificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDismissIntent(
        context: Context,
        notificationId: String,
        androidNotificationId: Int
    ): PendingIntent {
        val intent = Intent(context, NotificationDismissReceiver::class.java).apply {
            action = "com.blaink.NOTIFICATION_DISMISSED"
            putExtra("notificationID", notificationId)
            putExtra("androidNotificationId", androidNotificationId)
        }

        return PendingIntent.getBroadcast(
            context,
            androidNotificationId + 1, // Different request code for dismiss
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getSmallIcon(context: Context): Int {
        // Try to get app's notification icon, fallback to app icon
        val appInfo = context.applicationInfo
        return appInfo.icon
    }
}

/**
 * BroadcastReceiver for handling notification open events
 */
class NotificationOpenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notificationID")
        val deeplink = intent.getStringExtra("deeplink")

        Logger.d("Notification OPENED: $notificationId, deeplink: $deeplink")

        if (notificationId != null) {
            // Track open action
            PushNotificationManager.trackNotificationAction(notificationId, "opened")
        }

        // Launch the app's main activity
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notificationID", notificationId)
            putExtra("deeplink", deeplink)
            putExtra("notification_opened", true)
            // Copy all extras from the notification
            intent.extras?.let { extras ->
                putExtras(extras)
            }
        }

        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }

        // Notify delegate about notification open
        val notificationData = mutableMapOf<String, Any?>()
        intent.extras?.let { extras ->
            extras.keySet().forEach { key ->
                notificationData[key] = extras.get(key)
            }
        }
        PushNotificationManager.handleNotificationOpen(notificationData)
    }
}

/**
 * BroadcastReceiver for handling notification dismiss events
 */
class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notificationID")

        Logger.d("Notification DISMISSED: $notificationId")

        if (notificationId != null) {
            // Track dismiss action
            PushNotificationManager.trackNotificationAction(notificationId, "dismiss")
        }
    }
}
