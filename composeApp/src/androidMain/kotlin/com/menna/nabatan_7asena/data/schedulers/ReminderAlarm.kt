package com.menna.nabatan_7asena.data.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import java.util.Calendar
import java.util.TimeZone

internal fun prayerCalendar(timeZoneId: String): Calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))

internal fun AlarmManager.scheduleReminder(time: Long, operation: PendingIntent) {
    if (Build.VERSION.SDK_INT >= 31 && !canScheduleExactAlarms()) {
        setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, operation)
    } else {
        try { setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, operation) }
        catch (_: SecurityException) { setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, operation) }
    }
}
