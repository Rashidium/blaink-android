//
//  ScheduleLessonRequest.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.requests

import kotlinx.serialization.Serializable

/**
 * Request model for scheduling lessons
 */
@Serializable
data class ScheduleLessonRequest(
    val title: String,
    val description: String?,
    val startTime: String, // ISO 8601 format
    val endTime: String,   // ISO 8601 format
    val timezone: String
)
