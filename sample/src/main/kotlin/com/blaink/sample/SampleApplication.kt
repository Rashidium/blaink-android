//
//  SampleApplication.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.sample

import android.app.Application
import com.blaink.Blaink
import com.blaink.core.BlainkDelegate
import com.blaink.core.PushEnvironment

class SampleApplication : Application(), BlainkDelegate {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Blaink SDK
        val blaink = Blaink.getInstance()
        blaink.delegate = this
        blaink.setup(
            context = this,
            sdkKey = "eyJwbCI6IjEyQjE1RUQ1LTBBNzAtNDU2QS05RjRFLTlFQUNBMDk2QTEwQiJ9",
            environment = PushEnvironment.DEVELOPMENT,
            isDebugLogsEnabled = true
        )
    }
    
    override fun didReceiveNotification(notification: Map<String, Any>) {
        // Handle notification received
        println("📱 Notification received: $notification")
    }
    
    override fun didRegisterForBlainkNotifications(blainkUserId: String) {
        // Handle successful registration
        println("✅ Registered for Blaink notifications with user ID: $blainkUserId")
    }
}
