//
//  MainActivity.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blaink.Blaink
import com.blaink.core.localisation.localized

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register for push notifications (handles permission request on Android 13+)
        Blaink.getInstance().registerForRemoteNotifications(this)

        // Update localised text after SDK sync completes
        val tvLocalised = findViewById<TextView>(R.id.tvLocalised)
        Handler(Looper.getMainLooper()).postDelayed({
            tvLocalised.text = "accept_all".localized
        }, 2000)
    }
}
