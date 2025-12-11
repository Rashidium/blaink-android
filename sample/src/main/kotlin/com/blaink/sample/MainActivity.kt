//
//  MainActivity.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blaink.Blaink

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register for push notifications (handles permission request on Android 13+)
        Blaink.getInstance().registerForRemoteNotifications(this)
    }
}
