//
//  MainActivity.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.sample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.blaink.Blaink
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private val blaink = Blaink.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Get FCM token and register with Blaink
        setupPushNotifications()
        
        // Handle deeplink if present
        handleDeeplink(intent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Handle deeplink when app is already running
        handleDeeplink(intent)
    }
    
    private fun handleDeeplink(intent: Intent?) {
        intent?.data?.let { uri ->
            val handled = blaink.handleDeeplink(uri)
            if (handled) {
                Toast.makeText(
                    this,
                    "Deeplink handled: ${uri}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun setupPushNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("❌ Fetching FCM registration token failed: ${task.exception}")
                return@addOnCompleteListener
            }
            
            // Get new FCM registration token
            val token = task.result
            println("🔔 FCM Token: $token")
            
            // Register token with Blaink
            blaink.registerForRemoteNotifications(token)
            
            Toast.makeText(this, "FCM token registered with Blaink", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun testLogout() {
        lifecycleScope.launch {
            val result = blaink.logout()
            if (result.isSuccess) {
                Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Logout failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
