package com.menna.nabata_7asena.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GlobalAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private fun cancelAlarms(ids: List<Int>) {
        ids.forEach { id ->
            val intent = Intent(context, UnifiedAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager?.cancel(pendingIntent)
        }
    }

    fun cancelPrayerAlarms() {
        cancelAlarms(listOf(1001, 1002, 1003, 1004, 1005))
    }

    fun cancelAzkarAlarms() {
        cancelAlarms(listOf(
            AlarmConstants.ID_DHUHR_AZKAR,
            AlarmConstants.ID_ASR_AZKAR,
            AlarmConstants.ID_MAGHRIB_AZKAR,
            AlarmConstants.ID_ISHA_AZKAR,
            AlarmConstants.ID_FAJR_REMINDER,
            4001
        ))
    }

    fun cancelQuranAlarms() {
        cancelAlarms(listOf(
            AlarmConstants.ID_QURAN_DHUHR,
            AlarmConstants.ID_QURAN_ASR,
            AlarmConstants.ID_QURAN_MAGHRIB
        ))
    }

    fun cancelAllAlarms() {
        cancelPrayerAlarms()
        cancelAzkarAlarms()
        cancelQuranAlarms()
    }
}