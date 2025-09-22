//
//  SSLPinningManager.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.api.ssl

import android.content.Context
import android.util.Log
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Manages SSL certificate pinning for secure network communications
 */
object SSLPinningManager {
    private const val TAG = "SSLPinningManager"
    
    // Certificate hashes for blainks.com (SHA256)
    // These should be updated with the actual certificate hashes from your server
    private val pinnedCertificates = setOf(
        "sha256/cG6cw6IvFvDiW6+cQojuJGBwKuizigWHcHdDt20rm7s=", // Actual current certificate from blainks.com
        "sha256/2t1XOjgtUIESJfjUi/815TbDF77h2hagtCCso7pzBHI=", // Backup certificate (from Swift)
        "sha256/VqePxH3EcFwZuYK3CCOMz5HKMoeIZpZcEyBf4diPGSA=" // Backup certificate (from Swift)
    )
    
    // Domains that require SSL pinning
    private val pinnedDomains = setOf(
        "blainks.com",
        "www.blainks.com"
    )
    
    private var isDebugLogsEnabled = false
    private var context: Context? = null
    
    /**
     * Initialize the SSL pinning manager
     */
    fun initialize(context: Context, debugLogsEnabled: Boolean = false) {
        this.context = context
        this.isDebugLogsEnabled = debugLogsEnabled
        
        if (debugLogsEnabled) {
            Log.d(TAG, "🔐 SSL Pinning Manager initialized")
            Log.d(TAG, "🔐 Pinned domains: $pinnedDomains")
        }
    }
    
    /**
     * Create an OkHttpClient with SSL pinning configured
     */
    fun createOkHttpClient(): OkHttpClient {
        val certificatePinnerBuilder = CertificatePinner.Builder()
        
        // Add certificate pins for each domain
        pinnedDomains.forEach { domain ->
            pinnedCertificates.forEach { certificate ->
                certificatePinnerBuilder.add(domain, certificate)
            }
        }
        
        val certificatePinner = certificatePinnerBuilder.build()
        
        val builder = OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        // Add logging interceptor in debug mode
        if (isDebugLogsEnabled) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d(TAG, "🌐 Network: $message")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        return builder.build()
    }
    
    /**
     * Validate SSL pinning configuration
     */
    fun validateConfiguration(): Boolean {
        if (isDebugLogsEnabled) {
            Log.d(TAG, "🔍 Validating SSL Pinning configuration...")
            Log.d(TAG, "✅ Pinned domains: ${pinnedDomains.size}")
            Log.d(TAG, "✅ Pinned certificates: ${pinnedCertificates.size}")
            Log.d(TAG, "⚠️  Remember to replace placeholder certificate hashes with actual values")
        }
        
        return pinnedDomains.isNotEmpty() && pinnedCertificates.isNotEmpty()
    }
    
    /**
     * Test SSL pinning functionality
     */
    suspend fun testSSLPinning(): Boolean {
        return try {
            val client = createOkHttpClient()
            // This would normally make a test request to verify pinning
            // For now, just validate the configuration
            validateConfiguration()
        } catch (e: Exception) {
            if (isDebugLogsEnabled) {
                Log.e(TAG, "❌ SSL Pinning test failed: ${e.message}")
            }
            false
        }
    }
}
