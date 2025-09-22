//
//  BlainkDelegate.kt
//  Blaink
//
//  Prompted by Raşid Ramazanov using Cursor on 21.09.2025.
//

package com.blaink.core

/**
 * Delegate interface for Blaink SDK callbacks
 */
interface BlainkDelegate {
    /**
     * Called when a notification is received
     * @param notification The notification payload
     */
    fun didReceiveNotification(notification: Map<String, Any>)
    
    /**
     * Called when the device is successfully registered for Blaink notifications
     * @param blainkUserId The unique user ID assigned by Blaink
     */
    fun didRegisterForBlainkNotifications(blainkUserId: String)
}
