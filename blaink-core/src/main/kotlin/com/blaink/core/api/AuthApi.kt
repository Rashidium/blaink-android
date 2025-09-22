//
//  AuthApi.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api

import com.blaink.core.api.models.requests.ClientRequest
import com.blaink.core.api.models.requests.UpdateUserRequest
import com.blaink.core.api.models.responses.ClientResponse
import com.blaink.core.api.models.responses.EmptyResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Authentication API endpoints
 */
interface AuthApi {
    
    @POST("api/v1/client/init")
    suspend fun initSdk(@Body request: ClientRequest): Response<ClientResponse>
    
    @GET("api/v1/client/refresh")
    suspend fun refresh(@Header("Authorization") refreshToken: String): Response<ClientResponse>
    
    @PUT("api/v1/client/me")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<EmptyResponse>
    
    @GET("api/v1/client/me")
    suspend fun getCurrentUser(): Response<ClientResponse>
    
    @GET("api/v1/client/logout")
    suspend fun logout(): Response<EmptyResponse>
    
    @DELETE("api/client/me")
    suspend fun deleteAccount(): Response<EmptyResponse>
}
