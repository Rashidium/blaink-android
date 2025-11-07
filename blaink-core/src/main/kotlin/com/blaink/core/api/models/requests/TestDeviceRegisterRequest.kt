//
//  TestDeviceRegisterRequest.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 07.11.2025.
//

package com.blaink.core.api.models.requests

import kotlinx.serialization.Serializable

/**
 * Request model for test device registration
 */
@Serializable
data class TestDeviceRegisterRequest(
    val udid: String
)

