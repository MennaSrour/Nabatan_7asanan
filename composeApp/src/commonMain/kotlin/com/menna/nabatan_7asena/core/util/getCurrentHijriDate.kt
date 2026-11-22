package com.menna.nabatan_7asena.core.util

object DateUtils {
    fun currentHijriDate(offsetDays: Long = 0): HijriDate? = calculateHijriDate(offsetDays)

    fun getCurrentHijriDate(offsetDays: Long = 0): String =
        currentHijriDate(offsetDays)?.let { formatHijriDate(it.day, it.month, it.year) } ?: "التاريخ الهجري"
}

internal expect fun calculateHijriDate(offsetDays: Long): HijriDate?
