package com.menna.nabatan_7asena.data.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.core.alarm.AlarmConstants
import com.menna.nabatan_7asena.core.alarm.UnifiedAlarmReceiver
import com.menna.nabatan_7asena.data.local.room.AppDao
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class AzkarScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    suspend fun scheduleFixedAzkar(fajrTimeStr: String) {
        val stats = appDao.getUserStats().firstOrNull()
        if (stats?.azkarNotifications == false) return // المستخدم قافل التنبيه، منجدولش حاجة

        val now = System.currentTimeMillis()

        val slots = listOf(
            DataZekr(10, 0, "ذكر الله", "لا إله إلا الله", AlarmConstants.ID_AZKAR_1, R.raw.la_ilah_ila_allah),
            DataZekr(13, 12, "استغفر ربك", "استغفر الله العظيم", AlarmConstants.ID_AZKAR_2, R.raw.yala_zekr),
            DataZekr(14, 30, "ذكر الله", "الله أكبر", AlarmConstants.ID_AZKAR_3, R.raw.allahu_akbar),
            DataZekr(16, 0, "ذكر الله", "سبحان الله وبحمده", AlarmConstants.ID_AZKAR_4, R.raw.subhan_allah_wa_behamdeh),
            DataZekr(21, 0, "ذكر الله", "الحمدلله", AlarmConstants.ID_AZKAR_5, R.raw.alhamdulillah),
            DataZekr(15, 0, "ذكر الله", "سبحان الله", AlarmConstants.ID_AZKAR_6, R.raw.subhan_allah),
            DataZekr(22, 0, "ذكر الله", "لا حول ولا قوة إلا بالله", AlarmConstants.ID_AZKAR_7, R.raw.la_hawla)
        )

        slots.forEach { item ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, item.hour)
                set(Calendar.MINUTE, item.minute)
                set(Calendar.SECOND, 0)
            }
            if (cal.timeInMillis > now) {
                scheduleOne(cal.timeInMillis, item.title, item.msg, item.id, item.soundRes)
            }
        }

        try {
            val parts = fajrTimeStr.trim().split(" ")[0].split(":")
            val fajrCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, 60)
            }
            if (fajrCal.timeInMillis > now) {
                scheduleOne(
                    fajrCal.timeInMillis,
                    "أذكار الصباح",
                    "أصبحنا وأصبح الملك لله",
                    AlarmConstants.ID_AZKAR_MORNING, // ID مستقل، كان بيتصادم مع 2005 قبل كده
                    R.raw.yala_zekr
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleOne(time: Long, title: String, msg: String, id: Int, soundRes: Int) {
        val intent = Intent(context, UnifiedAlarmReceiver::class.java).apply {
            putExtra("TITLE", title)
            putExtra("MESSAGE", msg)
            putExtra("SOUND_ID", soundRes)
            putExtra("DESTINATION", "sebha")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class DataZekr(val hour: Int, val minute: Int, val title: String, val msg: String, val id: Int, val soundRes: Int)