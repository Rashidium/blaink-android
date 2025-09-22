//
//  ApnsNotificationRequest.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.requests

import kotlinx.serialization.Serializable

/**
 * Request model for APNS/FCM notification tracking
 */
@Serializable
data class ApnsNotificationRequest(
    val id: String,
    val action: String
)
