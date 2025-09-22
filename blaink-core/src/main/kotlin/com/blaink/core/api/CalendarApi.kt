//
//  CalendarApi.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api

import com.blaink.core.api.models.requests.ScheduleLessonRequest
import com.blaink.core.api.models.responses.CalendarScheduleResponse
import com.blaink.core.api.models.responses.EmptyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Calendar/scheduling API endpoints
 */
interface CalendarApi {
    
    @POST("api/v1/schedule")
    suspend fun scheduleLesson(@Body request: ScheduleLessonRequest): Response<EmptyResponse>
    
    @GET("api/v1/schedule")
    suspend fun getSchedule(): Response<CalendarScheduleResponse>
}
