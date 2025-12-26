///
/// LocalisationManager.kt
/// Created by Claude (Prompted by Rashid): 26/12/2024
///
/// Manages localisation sync and translation retrieval

package com.blaink.core.localisation

import com.blaink.core.api.BlainkApiClient
import com.blaink.core.utils.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manager for localisation sync and translation retrieval
 */
class LocalisationManager(
    private val sdkKey: String,
    language: String? = null
) {
    private val storage = LocalisationStorage
    private val syncMutex = Mutex()

    init {
        language?.let { storage.currentLanguage = it }
    }

    // MARK: - Language

    /**
     * Get current language
     */
    fun getLanguage(): String = storage.currentLanguage

    /**
     * Set current language
     */
    fun setLanguage(lang: String) {
        storage.currentLanguage = lang
    }

    /**
     * Set fallback string resources class for Android resource lookups
     * Call this to enable fallback to strings.xml when server value not found
     *
     * Usage:
     * ```
     * Blaink.getInstance().localisation.setFallbackResources(R.string::class.java)
     * ```
     *
     * @param stringResClass The R.string class from your app module
     */
    fun setFallbackResources(stringResClass: Class<*>) {
        storage.fallbackStringResources = stringResClass
    }

    // MARK: - Sync

    /**
     * Sync translations from server
     * @return Number of updated keys, or null if sync was skipped/failed
     */
    suspend fun sync(): Int? {
        if (!syncMutex.tryLock()) {
            return null
        }

        try {
            val currentVersion = storage.version

            val response = BlainkApiClient.localisationApi.sync(
                sdkKey = sdkKey,
                sinceVersion = currentVersion
            )

            if (response.isSuccessful) {
                val body = response.body() ?: return null

                // Apply updates
                storage.apply(
                    keys = body.keys,
                    deletedKeys = body.deletedKeys,
                    newVersion = body.version
                )

                val updatedCount = body.keys.size + (body.deletedKeys?.size ?: 0)

                Logger.d("[Blaink Localisation] Synced ${body.keys.size} keys, deleted ${body.deletedKeys?.size ?: 0}, version: ${body.version}")

                return updatedCount
            } else {
                Logger.e("[Blaink Localisation] Sync failed: ${response.errorBody()?.string()}")
                return null
            }
        } catch (e: Exception) {
            Logger.e("[Blaink Localisation] Sync failed", e)
            return null
        } finally {
            syncMutex.unlock()
        }
    }

    // MARK: - Get Translations

    /**
     * Get translation for key in current language
     * @param key Localisation key
     * @return Translated string or the key itself if not found
     */
    fun string(key: String): String {
        return storage.getString(key, storage.currentLanguage) ?: key
    }

    /**
     * Get translation for key in specified language
     * @param key Localisation key
     * @param lang Language code
     * @return Translated string or the key itself if not found
     */
    fun string(key: String, lang: String): String {
        return storage.getString(key, lang) ?: key
    }

    /**
     * Get translation for key, returning null if not found
     * @param key Localisation key
     * @return Translated string or null
     */
    fun optionalString(key: String): String? {
        return storage.getString(key, storage.currentLanguage)
    }

    /**
     * Get all translations for a key
     * @param key Localisation key
     * @return Map of [lang: value]
     */
    fun allTranslations(key: String): Map<String, String> {
        return storage.getTranslations(key)
    }

    // MARK: - Cache Info

    /**
     * Current cached version
     */
    val cachedVersion: Long
        get() = storage.version

    /**
     * Available languages in cache
     */
    val availableLanguages: List<String>
        get() = storage.languages

    /**
     * Clear all cached data
     */
    fun clearCache() {
        storage.clear()
    }
}
