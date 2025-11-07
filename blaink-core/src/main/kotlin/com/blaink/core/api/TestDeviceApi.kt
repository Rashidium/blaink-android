//
//  TestDeviceApi.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 07.11.2025.
//

package com.blaink.core.api

import com.blaink.core.api.models.requests.TestDeviceRegisterRequest
import com.blaink.core.api.models.responses.EmptyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Test device registration API endpoints
 */
interface TestDeviceApi {
    
    @POST("api/v1/test-device/register")
    suspend fun register(@Body request: TestDeviceRegisterRequest): Response<EmptyResponse>
}

