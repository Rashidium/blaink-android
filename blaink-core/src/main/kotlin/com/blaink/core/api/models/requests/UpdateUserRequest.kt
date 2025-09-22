//
//  UpdateUserRequest.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.requests

import com.blaink.core.PushEnvironment
import kotlinx.serialization.Serializable

/**
 * Request model for updating user information
 */
@Serializable
data class UpdateUserRequest(
    val pushNotificationToken: String? = null,
    val pushEnvironment: PushEnvironment? = null,
    val language: String? = null,
    val deviceName: String? = null
)
