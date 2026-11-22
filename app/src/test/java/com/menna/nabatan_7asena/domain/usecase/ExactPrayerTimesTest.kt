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

    @Test fun missingLocationNeverRequestsAConvenientDefaultCity() = runTest {
        assertNull(GetPrayerTimesUseCase(repository).at(null, "22-11-2026"))
        assertTrue(requests.isEmpty())
    }

    @Test fun coordinateRequestRetainsItsMethodTimezoneAndDate() = runTest {
        val request = com.menna.nabatan_7asena.domain.entity.PrayerRequest(
            com.menna.nabatan_7asena.domain.entity.PrayerPlace("مكة", "SA", 21.4225, 39.8262, "Asia/Riyadh"),
            com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod.UMM_AL_QURA)
        val coordinateRepository = object : PrayerTimesRepository {
            override suspend fun getPrayerTimes(requested: com.menna.nabatan_7asena.domain.entity.PrayerRequest, date: String): PrayerTimes {
                assertEquals(request, requested)
                assertEquals("22-11-2026", date)
                return times
            }
            override suspend fun getPrayerTimes(city: String, country: String, date: String): PrayerTimes = error("Must use coordinates")
            override suspend fun getExactPrayerTimes(city: String, country: String, date: String): PrayerTimes = error("Must use coordinates")
        }
        assertEquals(times, GetPrayerTimesUseCase(coordinateRepository).at(request, "22-11-2026"))
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
