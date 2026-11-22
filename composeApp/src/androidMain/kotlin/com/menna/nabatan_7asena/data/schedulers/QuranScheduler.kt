package com.menna.nabatan_7asena.data.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.core.alarm.AlarmConstants
import com.menna.nabatan_7asena.core.alarm.UnifiedAlarmReceiver
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import com.menna.nabatan_7asena.domain.repository.DailyActivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.ensureActive

class QuranScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyRepository: DailyActivityRepository,
    private val appDao: AppDao
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    suspend fun scheduleWerd(times: PrayerTimes, timeZoneId: String) {
        val stats = appDao.getUserStats().firstOrNull()
        if (stats?.quranNotifications == false) return // المستخدم قافل التنبيه، منجدولش حاجة

        val dayId = prayerCalendar(timeZoneId).get(Calendar.DAY_OF_YEAR)
        val plan = dailyRepository.getPlanForDay(dayId)

        scheduleOne(times.dhuhr, 20, plan?.quranWerd?.dhuhr, AlarmConstants.ID_QURAN_DHUHR, timeZoneId)
        scheduleOne(times.asr, 20, plan?.quranWerd?.asr, AlarmConstants.ID_QURAN_ASR, timeZoneId)
        scheduleOne(times.maghrib, 20, plan?.quranWerd?.maghrib, AlarmConstants.ID_QURAN_MAGHRIB, timeZoneId)
    }

    private suspend fun scheduleOne(timeStr: String, delayMin: Int, wird: String?, id: Int, timeZoneId: String) {
        kotlin.coroutines.coroutineContext.ensureActive()
        try {
            val parts = timeStr.trim().split(" ")[0].split(":")
            val cal = prayerCalendar(timeZoneId).apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MINUTE, delayMin)
            }

            if (cal.timeInMillis > System.currentTimeMillis()) {
                val intent = Intent(context, UnifiedAlarmReceiver::class.java).apply {
                    putExtra("TITLE", "ورد القرآن 📖")
                    putExtra("MESSAGE", wird ?: "وردك اليومي جاهز")
                    putExtra("SOUND_ID", R.raw.werd)
                    putExtra("DESTINATION", "quran")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager?.scheduleReminder(cal.timeInMillis, pendingIntent)
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}