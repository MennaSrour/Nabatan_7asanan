@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSBundle
import platform.UIKit.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

/** iOS owns its notification prompt text; show the localized app purpose beforehand. */
internal suspend fun confirmNotificationPurpose(): Boolean {
    val window = UIApplication.sharedApplication.connectedScenes.filterIsInstance<UIWindowScene>()
        .filter { it.activationState == UISceneActivationStateForegroundActive }
        .mapNotNull { it.keyWindow }.firstOrNull()
    var presenter = window?.rootViewController ?: return false
    while (presenter.presentedViewController != null) presenter = presenter.presentedViewController ?: break
    fun localized(key: String) = NSBundle.mainBundle.localizedStringForKey(key, key, "InfoPlist")
    return suspendCancellableCoroutine { continuation ->
        val alert = UIAlertController.alertControllerWithTitle("Nabatan", localized("NabataNotificationPurpose"), UIAlertControllerStyleAlert)
        alert.addAction(UIAlertAction.actionWithTitle(localized("NabataPermissionLater"), UIAlertActionStyleCancel) {
            if (continuation.isActive) continuation.resume(false)
        })
        alert.addAction(UIAlertAction.actionWithTitle(localized("NabataPermissionContinue"), UIAlertActionStyleDefault) {
            if (continuation.isActive) continuation.resume(true)
        })
        continuation.invokeOnCancellation {
            dispatch_async(dispatch_get_main_queue()) { alert.dismissViewControllerAnimated(false, null) }
        }
        presenter.presentViewController(alert, true, null)
    }
}
