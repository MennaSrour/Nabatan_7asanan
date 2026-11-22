package com.menna.nabatan_7asena.core.util

expect object DateUtils {
    fun currentHijriDate(offsetDays: Long = 0): HijriDate?
}

fun DateUtils.getCurrentHijriDate(offsetDays: Long = 0): String =
    currentHijriDate(offsetDays)?.let { formatHijriDate(it.day, it.month, it.year) } ?: "التاريخ الهجري"
