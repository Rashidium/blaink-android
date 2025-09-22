//
//  SecureStorage.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

/**
 * Secure storage implementation using Android EncryptedSharedPreferences
 */
object SecureStorage {
    private const val PREFS_NAME = "blaink_secure_prefs"
    private const val KEY_DEVICE_ID = "blaink_device_id"
    private const val KEY_CLIENT_ID = "blaink_client_id"
    private const val KEY_PUSH_TOKEN = "push_notification_token"
    private const val KEY_ACCESS_TOKEN = "blainks_access_token"
    private const val KEY_REFRESH_TOKEN = "blainks_refresh_token"
    
    private var encryptedPrefs: SharedPreferences? = null
    
    /**
     * Initialize secure storage
     */
    fun initialize(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences in case of encryption issues
            encryptedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Get Blaink device ID, creating one if it doesn't exist
     */
    fun getBlainkDeviceId(): String {
        return getOrCreate(KEY_DEVICE_ID)
    }
    
    /**
     * Get Blaink client ID, creating one if it doesn't exist
     */
    fun getBlainkClientId(): String {
        return getOrCreate(KEY_CLIENT_ID)
    }
    
    /**
     * Get or create a UUID for the given key
     */
    private fun getOrCreate(key: String): String {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        
        val existingId = prefs.getString(key, null)
        if (existingId != null) {
            return existingId
        }
        
        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(key, newId).apply()
        return newId
    }
    
    /**
     * Store push notification token
     */
    fun setPushNotificationToken(token: String?) {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        prefs.edit().putString(KEY_PUSH_TOKEN, token).apply()
    }
    
    /**
     * Get push notification token
     */
    fun getPushNotificationToken(): String? {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        return prefs.getString(KEY_PUSH_TOKEN, null)
    }
    
    /**
     * Store access token
     */
    fun setAccessToken(token: String?) {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    
    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Store refresh token
     */
    fun setRefreshToken(token: String?) {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Clear all stored data
     */
    fun clear() {
        val prefs = requireNotNull(encryptedPrefs) { "SecureStorage not initialized" }
        prefs.edit().clear().apply()
    }
}
