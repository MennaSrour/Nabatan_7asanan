package com.menna.nabatan_7asena.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.menna.nabatan.shared.R

actual object BroadcastNotificationManager {
    private const val PERMISSION_REQUESTED = "broadcast_notification_permission_requested"
    private val preferences get() = PlatformPreferences("app_prefs")

    actual fun initialize() {
        val context = AndroidPlatform.context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                context.getString(R.string.broadcast_notification_channel_id),
                context.getString(R.string.broadcast_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        subscribe()
    }

    internal fun subscribe() {
        if (FirebaseApp.getApps(AndroidPlatform.context).isEmpty()) return
        FirebaseMessaging.getInstance().subscribeToTopic(BROADCAST_TOPIC)
            .addOnFailureListener { Log.w("BroadcastNotifications", "FCM topic subscription failed", it) }
    }

    fun requestPermissionForReturningUser(launch: (String) -> Unit) {
        if (AndroidPermissions().notificationsGranted()) return
        val preferences = preferences
        if (!preferences.getBoolean("is_onboarding_completed", false)) return
        if (preferences.getBoolean(PERMISSION_REQUESTED, false)) return
        recordPermissionRequest()
        launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    internal fun recordPermissionRequest() {
        preferences.putBoolean(PERMISSION_REQUESTED, true)
    }
}
