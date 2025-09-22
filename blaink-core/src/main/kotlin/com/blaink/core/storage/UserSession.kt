//
//  UserSession.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.storage

/**
 * User session management
 */
object UserSession {
    
    /**
     * Current access token
     */
    var accessToken: String?
        get() = SecureStorage.getAccessToken()
        set(value) = SecureStorage.setAccessToken(value)
    
    /**
     * Current refresh token
     */
    var refreshToken: String?
        get() = SecureStorage.getRefreshToken()
        set(value) = SecureStorage.setRefreshToken(value)
    
    /**
     * Check if user is authenticated
     */
    val isAuthenticated: Boolean
        get() = accessToken != null
    
    /**
     * Clear user session
     */
    fun clear() {
        SecureStorage.setAccessToken(null)
        SecureStorage.setRefreshToken(null)
    }
}
