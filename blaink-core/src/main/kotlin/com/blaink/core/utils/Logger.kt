//
//  Logger.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core.utils

import android.util.Log

/**
 * Logging utilities for Blaink SDK
 */
object Logger {
    private const val TAG = "Blaink"
    
    var isDebugEnabled = false
    
    fun d(message: String) {
        if (isDebugEnabled) {
            Log.d(TAG, message)
        }
    }
    
    fun i(message: String) {
        if (isDebugEnabled) {
            Log.i(TAG, message)
        }
    }
    
    fun w(message: String) {
        if (isDebugEnabled) {
            Log.w(TAG, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null) {
        if (isDebugEnabled) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }
}
