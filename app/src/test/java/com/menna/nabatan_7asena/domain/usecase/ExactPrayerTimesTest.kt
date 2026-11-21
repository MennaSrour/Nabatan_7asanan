package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.GetPrayerTimesUseCase
import com.menna.nabatan_7asena.domain.PrayerTimesRepository
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ExactPrayerTimesTest {
    private val times = PrayerTimes("05:01", "12:00", "15:00", "18:00", "20:00")
    private val requests = mutableListOf<List<String>>()
    private var lookup: suspend () -> PrayerTimes = { times }
    private val repository = object : PrayerTimesRepository {
        override suspend fun getPrayerTimes(city: String, country: String, date: String): PrayerTimes = error("Forecast must not use stale fallback")
        override suspend fun getExactPrayerTimes(city: String, country: String, date: String): PrayerTimes {
            requests += listOf(city, country, date)
            return lookup()
        }
    }

    @Test fun requestsTheExactDayAndLocationThroughTheExistingRepository() = runTest {
        assertEquals(times, GetPrayerTimesUseCase(repository).exact("Cairo", "Egypt", "22-11-2026"))
        assertEquals(listOf(listOf("Cairo", "Egypt", "22-11-2026")), requests)
    }

    @Test fun failedForecastRemainsUnknownInsteadOfUsingAnotherDaysTimes() = runTest {
        lookup = { throw IllegalStateException("offline") }
        assertNull(GetPrayerTimesUseCase(repository).exact("Cairo", "Egypt", "22-11-2026"))
    }

    @Test fun cancellationRemainsCancellation() = runTest {
        lookup = { throw CancellationException("stopped") }
        try {
            GetPrayerTimesUseCase(repository).exact("Cairo", "Egypt", "22-11-2026")
            fail("Expected cancellation")
        } catch (expected: CancellationException) {
            assertEquals("stopped", expected.message)
        }
    }
}
