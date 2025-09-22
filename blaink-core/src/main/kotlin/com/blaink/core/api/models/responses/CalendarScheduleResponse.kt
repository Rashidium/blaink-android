//
//  CalendarScheduleResponse.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.models.responses

import kotlinx.serialization.Serializable

/**
 * Response model for calendar schedule operations
 */
@Serializable
data class CalendarScheduleResponse(
    val events: List<CalendarEvent>
) {
    @Serializable
    data class CalendarEvent(
        val id: String,
        val title: String,
        val description: String?,
        val startTime: String,
        val endTime: String,
        val timezone: String
    )
}
