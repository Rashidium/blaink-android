//
//  ClientRequest.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.requests

import com.blaink.core.PushEnvironment
import kotlinx.serialization.Serializable

/**
 * Request model for client initialization
 */
@Serializable
data class ClientRequest(
    val clientId: String,
    val sdkKey: String,
    val device: Device
) {
    @Serializable
    data class Device(
        val deviceId: String,
        val deviceName: String,
        val platform: String,
        val language: String?,
        val pushNotificationToken: String?,
        val pushEnvironment: PushEnvironment
    )
}
