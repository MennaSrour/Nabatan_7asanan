package com.menna.nabatan_7asena.core.util

import android.icu.util.Calendar
import android.icu.util.IslamicCalendar

actual object DateUtils {
    actual fun currentHijriDate(offsetDays: Long): HijriDate? {
        require(offsetDays in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong())
        val calendar = IslamicCalendar().apply {
            calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
            add(Calendar.DAY_OF_MONTH, offsetDays.toInt())
        }
        return HijriDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }
}
