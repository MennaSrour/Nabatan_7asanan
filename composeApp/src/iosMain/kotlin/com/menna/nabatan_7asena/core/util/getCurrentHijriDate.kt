@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.core.util

import platform.Foundation.*

internal actual fun calculateHijriDate(offsetDays: Long): HijriDate? {
    val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierIslamicUmmAlQura)
        ?: return null
    val date = calendar.dateByAddingUnit(NSCalendarUnitDay, offsetDays, NSDate(), 0u)
        ?: return null
    val parts = calendar.components(NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear, date)
    return HijriDate(parts.day.toInt(), parts.month.toInt(), parts.year.toInt())
}
