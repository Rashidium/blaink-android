//
//  ClientResponse.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.responses

import kotlinx.serialization.Serializable

/**
 * Response model for client operations
 */
@Serializable
data class ClientResponse(
    val error: Boolean,
    val body: ClientBody,
    val reason: String
) {
    @Serializable
    data class ClientBody(
        val id: String,
        val accessToken: String,
        val refreshToken: String,
        val clientId: String,
        val pushNotificationEnabled: Boolean
    )
}
