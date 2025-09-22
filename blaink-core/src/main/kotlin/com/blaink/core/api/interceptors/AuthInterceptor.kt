//
//  AuthInterceptor.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.interceptors

import android.util.Base64
import com.blaink.core.storage.UserSession
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add authentication tokens to API requests
 */
class AuthInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add Basic Auth for client/init endpoint (matches Swift implementation)
        if (originalRequest.url.encodedPath.contains("/client/init")) {
            val basicAuth = createBasicAuthHeader()
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Basic $basicAuth")
                .build()
            return chain.proceed(authenticatedRequest)
        }
        
        // Skip auth for refresh endpoint
        if (originalRequest.url.encodedPath.contains("/client/refresh")) {
            // Refresh endpoint uses refresh token (implement later)
            return chain.proceed(originalRequest)
        }
        
        // Add access token if available
        val accessToken = UserSession.accessToken
        if (accessToken != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            return chain.proceed(authenticatedRequest)
        }
        
        return chain.proceed(originalRequest)
    }
    
    private fun createBasicAuthHeader(): String {
        // Credentials from Swift implementation (AUTH.swift basicHeaders())
        val username = "Blaink"
        val password = "Blaink!@2025"
        val credentials = "$username:$password"
        return Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}