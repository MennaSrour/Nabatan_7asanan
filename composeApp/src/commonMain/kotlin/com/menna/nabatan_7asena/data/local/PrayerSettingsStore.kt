package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString


interface PrayerSettingsStorage {
    fun read(): String?
    fun write(value: String)
}

private class PreferencesPrayerSettingsStorage : PrayerSettingsStorage {
    private val prefs by lazy { PlatformPreferences("prayer_settings") }
    override fun read() = prefs.getString("settings", null)
    override fun write(value: String) = prefs.putString("settings", value)
}

class PrayerSettingsRepository(private val storage: PrayerSettingsStorage) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val mutable = MutableStateFlow(read())
    val state = mutable.asStateFlow()
    private fun read(): PrayerSettings = try {
        json.decodeFromString<PrayerSettings>(storage.read() ?: "{}")
    } catch (e: kotlinx.coroutines.CancellationException) { throw e }
    catch (_: Exception) { PrayerSettings() }
    private fun save(value: PrayerSettings) {
        storage.write(json.encodeToString(value))
        mutable.value = value
    }
    fun selectCity(city: PrayerPlace?) = save(mutable.value.copy(selectedCity = city))
    fun saveGps(place: PrayerPlace) {
        val previous = mutable.value.savedGps
        // Ignore sub-kilometre GPS jitter so a background refresh cannot continually replace itself.
        if (previous != null && previous.countryCode == place.countryCode && previous.timeZoneId == place.timeZoneId &&
            kotlin.math.abs(previous.latitude - place.latitude) < 0.005 &&
            kotlin.math.abs(previous.longitude - place.longitude) < 0.005) return
        save(mutable.value.copy(savedGps = place))
    }
    fun setMethod(method: PrayerCalculationMethod?) = save(mutable.value.copy(method = method))
    fun setHijriOffset(offset: Int) { require(offset in -2..2); save(mutable.value.copy(hijriDateOffset = offset)) }
}

object PrayerSettingsStore {
    private val repository by lazy { PrayerSettingsRepository(PreferencesPrayerSettingsStorage()) }
    val state get() = repository.state
    fun selectCity(city: PrayerPlace?) = repository.selectCity(city)
    fun saveGps(place: PrayerPlace) = repository.saveGps(place)
    fun setMethod(method: PrayerCalculationMethod?) = repository.setMethod(method)
    fun setHijriOffset(offset: Int) = repository.setHijriOffset(offset)
}

val prayerCityChoices = listOf(
    PrayerPlace("القاهرة", "EG", 30.0444, 31.2357, "Africa/Cairo"),
    PrayerPlace("الإسكندرية", "EG", 31.2001, 29.9187, "Africa/Cairo"),
    PrayerPlace("مكة المكرمة", "SA", 21.4225, 39.8262, "Asia/Riyadh"),
    PrayerPlace("المدينة المنورة", "SA", 24.4672, 39.6024, "Asia/Riyadh"),
    PrayerPlace("الرياض", "SA", 24.7136, 46.6753, "Asia/Riyadh"),
    PrayerPlace("دبي", "AE", 25.2048, 55.2708, "Asia/Dubai"),
    PrayerPlace("عمان", "JO", 31.9539, 35.9106, "Asia/Amman"),
    PrayerPlace("القدس", "PS", 31.7683, 35.2137, "Asia/Hebron"),
    PrayerPlace("لندن", "GB", 51.5074, -0.1278, "Europe/London"),
    PrayerPlace("نيويورك", "US", 40.7128, -74.0060, "America/New_York"),
    PrayerPlace("تورونتو", "CA", 43.6532, -79.3832, "America/Toronto")
)
