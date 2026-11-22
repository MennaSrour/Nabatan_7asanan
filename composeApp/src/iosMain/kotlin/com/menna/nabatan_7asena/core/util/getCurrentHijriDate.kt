@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.core.util

import platform.Foundation.*

actual object DateUtils {
    actual fun getCurrentHijriDate(offsetDays: Long): String {
        require(offsetDays in -2L..2L)
        val date = currentHijriParts(offsetDays.toInt())
        return formatHijriDate(date.day, date.month, date.year)
    }
    actual fun currentHijriParts(offsetDays: Int): HijriDate {
        require(offsetDays in -2..2)
        val calendar = requireNotNull(NSCalendar.calendarWithIdentifier(NSCalendarIdentifierIslamicUmmAlQura))
        val date = requireNotNull(calendar.dateByAddingUnit(NSCalendarUnitDay, offsetDays.toLong(), NSDate(), 0u))
        val parts = calendar.components(NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear, date)
        return HijriDate(parts.day.toInt(), parts.month.toInt(), parts.year.toInt())
    }
}
