@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.core.util

import platform.Foundation.*

actual object DateUtils {
    actual fun getCurrentHijriDate(offsetDays: Long): String {
        val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierIslamicUmmAlQura)
            ?: return "التاريخ الهجري"
        val date = calendar.dateByAddingUnit(NSCalendarUnitDay, offsetDays, NSDate(), 0u)
            ?: return "التاريخ الهجري"
        val parts = calendar.components(NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear, date)
        return formatHijriDate(parts.day.toInt(), parts.month.toInt(), parts.year.toInt())
    }
}
