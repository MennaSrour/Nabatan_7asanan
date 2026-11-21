@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.menna.nabatan_7asena.presentation.screens.home

import kotlinx.datetime.*
import kotlin.time.Instant

internal data class CountdownPrayer(val name: String, val minutes: Int)
internal data class PrayerCountdown(val name: String, val secondsRemaining: Long, val elapsedFraction: Float?)

internal fun nextPrayerCountdown(
    prayers: List<CountdownPrayer>, date: LocalDate, previousIshaMinutes: Int?, tomorrowFajrMinutes: Int?,
    fajrName: String, now: Instant, zone: TimeZone
): PrayerCountdown? {
    fun time(day: LocalDate, minutes: Int) = day.atTime(minutes / 60, minutes % 60).toInstant(zone).epochSeconds
    val events = buildList {
        previousIshaMinutes?.takeIf { it in 0..1439 }?.let { add("" to time(date.minus(1, DateTimeUnit.DAY), it)) }
        prayers.filter { it.minutes in 0..1439 }.forEach { add(it.name to time(date, it.minutes)) }
        tomorrowFajrMinutes?.takeIf { it in 0..1439 }?.let { add(fajrName to time(date.plus(1, DateTimeUnit.DAY), it)) }
    }.sortedBy { it.second }
    val next = events.firstOrNull { it.second > now.epochSeconds && it.first.isNotEmpty() } ?: return null
    val previous = events.lastOrNull { it.second <= now.epochSeconds }
    val fraction = previous?.let {
        ((now.epochSeconds - it.second).toDouble() / (next.second - it.second)).toFloat().coerceIn(0f, 1f)
    }
    return PrayerCountdown(next.first, next.second - now.epochSeconds, fraction)
}

internal fun formatPrayerCountdown(seconds: Long): String {
    val value = seconds.coerceAtLeast(0)
    return listOf(value / 3600, value % 3600 / 60, value % 60).joinToString(":") { it.toString().padStart(2, '0') }
}
