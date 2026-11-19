package com.menna.nabatan_7asena.core.alarm

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

internal object RetiredDailyMission {
    private const val LEGACY_REQUEST_CODE = 4001
    private const val LEGACY_TITLE = "تحدي اليوم يا بطل 💪"

    fun matches(title: String?): Boolean = title == LEGACY_TITLE

    fun cancel(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(context, LEGACY_REQUEST_CODE,
            Intent(context, UnifiedAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null) {
            context.getSystemService(AlarmManager::class.java)?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        val notifications = context.getSystemService(NotificationManager::class.java) ?: return
        notifications.activeNotifications.filter {
            matches(it.notification.extras.getCharSequence(Notification.EXTRA_TITLE)?.toString())
        }.forEach { notifications.cancel(it.tag, it.id) }
    }
}
