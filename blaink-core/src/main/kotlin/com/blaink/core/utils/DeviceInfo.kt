//
//  DeviceInfo.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.utils

import android.content.Context
import android.os.Build
import com.blaink.core.PushEnvironment
import com.blaink.core.api.models.requests.ClientRequest
import com.blaink.core.storage.SecureStorage
import java.util.Locale

/**
 * Device information utilities
 */
object DeviceInfo {
    
    /**
     * Gather device information for API requests
     */
    fun gather(context: Context, pushEnvironment: PushEnvironment): ClientRequest.Device {
        return ClientRequest.Device(
            deviceId = SecureStorage.getBlainkDeviceId(),
            deviceName = getDeviceName(),
            platform = "Android",
            language = getLanguageCode(),
            pushNotificationToken = SecureStorage.getPushNotificationToken(),
            pushEnvironment = pushEnvironment
        )
    }
    
    /**
     * Get device name (manufacturer + model)
     */
    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        
        return if (model.startsWith(manufacturer)) {
            model.replaceFirstChar { it.uppercase() }
        } else {
            "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
        }
    }
    
    /**
     * Get language code
     */
    private fun getLanguageCode(): String? {
        return try {
            Locale.getDefault().language
        } catch (e: Exception) {
            null
        }
    }
}
