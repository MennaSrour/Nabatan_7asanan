package com.menna.nabatan_7asena.core.util

expect object DateUtils {
    fun getCurrentHijriDate(offsetDays: Long = 0): String
}
