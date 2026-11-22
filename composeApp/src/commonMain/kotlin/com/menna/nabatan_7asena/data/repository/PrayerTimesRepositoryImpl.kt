package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.core.util.PrayerTimeline
import com.menna.nabatan_7asena.data.PrayerApiResponse
import com.menna.nabatan_7asena.data.PrayerTimesApi
import com.menna.nabatan_7asena.data.toEntity
import com.menna.nabatan_7asena.domain.PrayerTimesRepository
import com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod
import com.menna.nabatan_7asena.domain.entity.PrayerRequest
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrayerTimesRepositoryImpl(private val api: PrayerTimesApi) : PrayerTimesRepository {
    private val prefs = PlatformPreferences("prayer_times_cache")
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheMutex = Mutex()

    override suspend fun getPrayerTimes(city: String, country: String, date: String): PrayerTimes =
        getExactPrayerTimes(city, country, date)

    override suspend fun getExactPrayerTimes(city: String, country: String, date: String): PrayerTimes =
        cached("$date|$city|$country|${PrayerCalculationMethod.forCountry(country).apiId}") {
            api.getPrayerTimesByCity(date = date, city = city, country = country)
        }

    override suspend fun getPrayerTimes(request: PrayerRequest, date: String): PrayerTimes =
        cached("$date|${request.signature}") { api.getPrayerTimes(request, date) }

    /** Never substitute another date, coordinate, method or timezone after a network failure. */
    private suspend fun cached(key: String, load: suspend () -> PrayerApiResponse): PrayerTimes {
        cacheMutex.withLock { readCache()[key] }?.let { return it }
        val response = load()
        check(response.code == 200) { "Prayer service unavailable" }
        val fresh = response.toEntity()
        require(listOf(fresh.fajr, fresh.dhuhr, fresh.asr, fresh.maghrib, fresh.isha).all {
            PrayerTimeline.parseMinutes(it) != null
        }) { "Invalid prayer times" }
        cacheMutex.withLock {
            val entries = readCache().toMutableMap().apply { put(key, fresh) }.entries.toList().takeLast(12)
            prefs.putString("countdown_exact_dates", json.encodeToString(entries.associate { it.key to it.value }))
        }
        return fresh
    }

    private fun readCache(): Map<String, PrayerTimes> = try {
        json.decodeFromString(prefs.getString("countdown_exact_dates", null) ?: "{}")
    } catch (e: CancellationException) { throw e }
    catch (_: Exception) { emptyMap() }
}
