@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.menna.nabatan_7asena.presentation.screens.home

import kotlinx.datetime.*
import kotlin.test.*
import kotlin.time.Instant

class PrayerCountdownTest {
    private val date = LocalDate(2026, 11, 21)
    private val zone = TimeZone.UTC
    private val prayers = listOf(
        CountdownPrayer("الفجر", 300), CountdownPrayer("الظهر", 720), CountdownPrayer("العصر", 900),
        CountdownPrayer("المغرب", 1080), CountdownPrayer("العشاء", 1200)
    )

    private fun resolve(at: String, previous: Int? = 1200, next: Int? = 301) = nextPrayerCountdown(
        prayers, date, previous, next, "الفجر", Instant.parse(at), zone)

    @Test fun countdownIncludesLiveSecondsAndActualIntervalProgress() {
        val result = assertNotNull(resolve("2026-11-21T13:35:45Z"))
        assertEquals("العصر", result.name)
        assertEquals("01:24:15", formatPrayerCountdown(result.secondsRemaining))
        assertEquals(5745f / 10800f, assertNotNull(result.elapsedFraction), 0.0001f)
    }

    @Test fun exactlyAtPrayerTimeAdvancesToTheNextPrayerAndResetsTimeline() {
        val result = assertNotNull(resolve("2026-11-21T15:00:00Z"))
        assertEquals("المغرب", result.name)
        assertEquals(10800, result.secondsRemaining.toInt())
        assertEquals(0f, result.elapsedFraction)
    }

    @Test fun afterIshaUsesTomorrowsActualFajrRatherThanTodaysTime() {
        val result = assertNotNull(resolve("2026-11-21T23:00:00Z"))
        assertEquals("الفجر", result.name)
        assertEquals("06:01:00", formatPrayerCountdown(result.secondsRemaining))
        assertEquals(10800f / 32460f, assertNotNull(result.elapsedFraction), 0.0001f)
    }

    @Test fun preFajrProgressUsesPreviousDaysIshaAcrossMidnight() {
        val result = assertNotNull(resolve("2026-11-21T02:00:00Z"))
        assertEquals("03:00:00", formatPrayerCountdown(result.secondsRemaining))
        assertEquals(2f / 3f, assertNotNull(result.elapsedFraction), 0.0001f)
    }

    @Test fun missingTomorrowsTimeDoesNotFabricateANextDayCountdown() {
        assertNull(resolve("2026-11-21T23:00:00Z", next = null))
    }

    @Test fun missingPreviousTimeDoesNotInventElapsedPercentage() {
        val result = assertNotNull(resolve("2026-11-21T02:00:00Z", previous = null))
        assertEquals(10800, result.secondsRemaining.toInt())
        assertNull(result.elapsedFraction)
    }

    @Test fun oldSchedulesDoNotRecycleForeverAfterTheDayChanges() {
        assertNull(resolve("2026-11-23T00:00:00Z"))
    }

    @Test fun accountsForDaylightSavingChangesInActualElapsedTime() {
        val newYork = TimeZone.of("America/New_York")
        val result = assertNotNull(nextPrayerCountdown(prayers, LocalDate(2026, 3, 8), 1200, 300, "الفجر",
            Instant.parse("2026-03-08T08:00:00Z"), newYork))
        assertEquals("01:00:00", formatPrayerCountdown(result.secondsRemaining))
        assertEquals(7f / 8f, assertNotNull(result.elapsedFraction), 0.0001f)
    }

    @Test fun handlesUnsortedAndMalformedTimingValuesWithoutCrashing() {
        val result = assertNotNull(nextPrayerCountdown(prayers.reversed() + CountdownPrayer("invalid", -1), date,
            -1, 1500, "الفجر", Instant.parse("2026-11-21T13:00:00Z"), zone))
        assertEquals("العصر", result.name)
        assertEquals("02:00:00", formatPrayerCountdown(result.secondsRemaining))
    }

    @Test fun formatsZeroAndNeverDisplaysNegativeTime() {
        assertEquals("00:00:00", formatPrayerCountdown(0))
        assertEquals("00:00:00", formatPrayerCountdown(-1))
    }
}
