//
//  BlainkApiClient.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api

import com.blaink.core.api.interceptors.AuthInterceptor
import com.blaink.core.api.ssl.SSLPinningManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Main API client for Blaink SDK network operations
 */
object BlainkApiClient {
    private const val BASE_URL = "https://blainks.com/"
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    private val okHttpClient by lazy {
        SSLPinningManager.createOkHttpClient()
            .newBuilder()
            .addInterceptor(AuthInterceptor())
            .build()
    }
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val apnsApi: ApnsApi by lazy { retrofit.create(ApnsApi::class.java) }
    val calendarApi: CalendarApi by lazy { retrofit.create(CalendarApi::class.java) }
    val testDeviceApi: TestDeviceApi by lazy { retrofit.create(TestDeviceApi::class.java) }
}
