package com.menna.nabata_7asena.domain.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.core.alarm.UnifiedAlarmReceiver
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class TaskScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyRepository: DailyActivityRepository 
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    
    suspend fun scheduleDailyChallenge() {
        withContext(Dispatchers.IO) {
            
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9) 
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val plan = dailyRepository.getPlanForDay(dayOfYear)
            val challengeText = plan?.challengeTitle ?: "تحدي جديد بانتظارك! افتح التطبيق لتعرف 🎁"

            
            val intent = Intent(context, UnifiedAlarmReceiver::class.java).apply {
                putExtra("TITLE", "تحدي اليوم يا بطل 💪")
                putExtra("MESSAGE", challengeText)
                
                putExtra("SOUND_ID", R.raw.challenge_sound)
                putExtra("DESTINATION", "home") 
            }

            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                4001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}