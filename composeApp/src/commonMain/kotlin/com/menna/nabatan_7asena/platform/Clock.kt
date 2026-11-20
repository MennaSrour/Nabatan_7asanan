@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.menna.nabatan_7asena.platform

import kotlinx.datetime.*
import kotlin.time.Clock

object AppClock {
    fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
    fun today(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    fun dateString(date: LocalDate = today()): String = "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthNumber.toString().padStart(2, '0')}-${date.year}"
    fun dayOfYear(): Int = today().dayOfYear
    fun hour(): Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    fun nowMinutes(): Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let { it.hour * 60 + it.minute }
    fun isYesterday(value: String): Boolean = value == dateString(today().minus(1, DateTimeUnit.DAY))
}
