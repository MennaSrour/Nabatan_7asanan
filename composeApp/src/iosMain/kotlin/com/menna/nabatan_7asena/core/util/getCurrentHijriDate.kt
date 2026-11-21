@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.core.util

import platform.Foundation.*

actual object DateUtils {
    actual fun getCurrentHijriDate(offsetDays: Long): String {
        val formatter = NSDateFormatter()
        formatter.locale = NSLocale("ar_EG")
        formatter.calendar =
            NSCalendar.calendarWithIdentifier("islamic-umalqura") ?: NSCalendar.currentCalendar
        formatter.dateFormat = "d MMMM yyyy"
        return formatter.stringFromDate(NSDate.dateWithTimeIntervalSinceNow(offsetDays * 86_400.0))
    }
}
