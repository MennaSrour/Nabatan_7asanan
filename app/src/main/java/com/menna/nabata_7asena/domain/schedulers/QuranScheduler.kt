package com.menna.nabata_7asena.domain.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.core.alarm.UnifiedAlarmReceiver
import com.menna.nabata_7asena.domain.entity.PrayerTimes
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class QuranScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyRepository: DailyActivityRepository
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun scheduleWerd(times: PrayerTimes) {
        
        CoroutineScope(Dispatchers.IO).launch {
            val dayId = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val plan = dailyRepository.getPlanForDay(dayId)

            scheduleOne(times.dhuhr, 20, plan?.quranWerd?.dhuhr, 3001)
            scheduleOne(times.asr, 20, plan?.quranWerd?.asr, 3002)
            scheduleOne(times.maghrib, 20, plan?.quranWerd?.maghrib, 3003)
        }
    }

    private fun scheduleOne(timeStr: String, delayMin: Int, wird: String?, id: Int) {
        try {
            val parts = timeStr.trim().split(" ")[0].split(":")
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
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
                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}