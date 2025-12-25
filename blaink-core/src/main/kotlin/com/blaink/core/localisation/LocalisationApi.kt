///
/// LocalisationApi.kt
/// Created by Claude (Prompted by Rashid): 26/12/2024
///
/// API endpoints for localisation sync

package com.blaink.core.localisation

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Localisation sync API endpoint
 */
interface LocalisationApi {

    /**
     * Sync localisation keys from server
     * @param sdkKey SDK key for authentication
     * @param sinceVersion Version to fetch updates from (0 = all)
     */
    @GET("api/v1/localise")
    suspend fun sync(
        @Header("X-SDK-Key") sdkKey: String,
        @Query("since_version") sinceVersion: Long = 0
    ): Response<LocalisationSyncResponse>
}

/**
 * Response model for localisation sync
 */
@Serializable
data class LocalisationSyncResponse(
    val version: Long,
    val keys: Map<String, Map<String, String>>,
    val deletedKeys: List<String>? = null
)
