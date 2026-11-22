package com.menna.nabatan_7asena.core.util

import android.icu.util.Calendar
import android.icu.util.IslamicCalendar

actual object DateUtils {
    actual fun getCurrentHijriDate(offsetDays: Long): String {
        require(offsetDays in -2L..2L)
        val date = currentHijriParts(offsetDays.toInt())
        return formatHijriDate(date.day, date.month, date.year)
    }
    actual fun currentHijriParts(offsetDays: Int): HijriDate {
        require(offsetDays in -2..2)
        val calendar = IslamicCalendar().apply {
            calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
            add(Calendar.DAY_OF_MONTH, offsetDays)
        }
        return HijriDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }
}
