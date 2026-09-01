package com.menna.nabatan_7asena.core.alarm

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
        cancelAlarms(listOf(
            AlarmConstants.ID_FAJR,
            AlarmConstants.ID_DHUHR,
            AlarmConstants.ID_ASR,
            AlarmConstants.ID_MAGHRIB,
            AlarmConstants.ID_ISHA
        ))
    }

    fun cancelAzkarAlarms() {
        cancelAlarms(listOf(
            AlarmConstants.ID_AZKAR_1,
            AlarmConstants.ID_AZKAR_2,
            AlarmConstants.ID_AZKAR_3,
            AlarmConstants.ID_AZKAR_4,
            AlarmConstants.ID_AZKAR_5,
            AlarmConstants.ID_AZKAR_6,
            AlarmConstants.ID_AZKAR_7,
            AlarmConstants.ID_AZKAR_MORNING
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