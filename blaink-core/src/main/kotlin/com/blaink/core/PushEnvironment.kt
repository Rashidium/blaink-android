//
//  PushEnvironment.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Push notification environment enumeration
 */
@Serializable
enum class PushEnvironment(val value: String) {
    @SerialName("development")
    DEVELOPMENT("development"),
    
    @SerialName("production")
    PRODUCTION("production")
}
