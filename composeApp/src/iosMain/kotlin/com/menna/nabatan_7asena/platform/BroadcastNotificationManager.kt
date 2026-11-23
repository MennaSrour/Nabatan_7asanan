package com.menna.nabatan_7asena.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual object BroadcastNotificationManager {
    val topic: String get() = BROADCAST_TOPIC

    /** Registration is separate from permission: never prompt at app launch. */
    @OptIn(ExperimentalForeignApi::class)
    actual fun initialize() {
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            if (settings?.authorizationStatus in listOf(UNAuthorizationStatusAuthorized,
                    UNAuthorizationStatusProvisional, UNAuthorizationStatusEphemeral)) {
                dispatch_async(dispatch_get_main_queue()) {
                    UIApplication.sharedApplication.registerForRemoteNotifications()
                }
            }
        }
    }
}
