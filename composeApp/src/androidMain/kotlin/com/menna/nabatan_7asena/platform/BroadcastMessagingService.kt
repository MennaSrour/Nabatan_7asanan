package com.menna.nabatan_7asena.platform

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.menna.nabatan.shared.R

class BroadcastMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        BroadcastNotificationManager.subscribe()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // FCM displays notification payloads in the background using the manifest defaults.
        // This callback displays them in the foreground and handles delivered data messages.
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        if (title.isNullOrBlank() && body.isNullOrBlank()) return

        val manager = getSystemService(NotificationManager::class.java) ?: return
        if (!AndroidPermissions().notificationsGranted() || !manager.areNotificationsEnabled()) return

        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        val notification = NotificationCompat.Builder(this, getString(R.string.broadcast_notification_channel_id))
            .setSmallIcon(R.drawable.ic_broadcast_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(message.messageId ?: java.util.UUID.randomUUID().toString(), 0, notification)
    }
}
