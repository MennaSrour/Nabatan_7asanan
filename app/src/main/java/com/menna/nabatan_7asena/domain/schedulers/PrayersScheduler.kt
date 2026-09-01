package com.menna.nabatan_7asena.domain.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.core.alarm.AlarmConstants
import com.menna.nabatan_7asena.core.alarm.UnifiedAlarmReceiver
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import javax.inject.Inject

class PrayersScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    suspend fun scheduleDailyPrayers(prayerTimes: PrayerTimes) {
        val stats = appDao.getUserStats().firstOrNull()
        if (stats?.prayerNotifications == false) return // المستخدم قافل التنبيه، منجدولش حاجة

        scheduleOne("الفجر", prayerTimes.fajr, R.raw.fajr, AlarmConstants.ID_FAJR)
        scheduleOne("الظهر", prayerTimes.dhuhr, R.raw.zuhr, AlarmConstants.ID_DHUHR)
        scheduleOne("العصر", prayerTimes.asr, R.raw.asr, AlarmConstants.ID_ASR)
        scheduleOne("المغرب", prayerTimes.maghrib, R.raw.sound_normal, AlarmConstants.ID_MAGHRIB)
        scheduleOne("العشاء", prayerTimes.isha, R.raw.ishaa, AlarmConstants.ID_ISHA)
    }

    private fun scheduleOne(name: String, timeStr: String, soundRes: Int, id: Int) {
        val time = parseTime(timeStr)
        if (time > System.currentTimeMillis()) {
            val intent = Intent(context, UnifiedAlarmReceiver::class.java).apply {
                putExtra("TITLE", "الصلاة")
                putExtra("MESSAGE", "حان الآن موعد صلاة $name 🕌")
                putExtra("SOUND_ID", soundRes)
                putExtra("DESTINATION", "home")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
                )
                Log.d("PrayersScheduler", "تم جدولة صلاة $name")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseTime(timeString: String): Long {
        val cleanTime = timeString.trim().split(" ")[0]
        val parts = cleanTime.split(":")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }
}