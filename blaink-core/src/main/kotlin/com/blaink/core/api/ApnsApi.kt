//
//  ApnsApi.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api

import com.blaink.core.api.models.requests.ApnsNotificationRequest
import com.blaink.core.api.models.responses.EmptyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

/**
 * APNS/FCM notification tracking API endpoints
 */
interface ApnsApi {
    
    @PUT("api/v1/client/updateNotification")
    suspend fun updateNotification(@Body request: ApnsNotificationRequest): Response<EmptyResponse>
}
