package com.menna.nabatan_7asena.domain.entity

import com.menna.nabatan_7asena.data.local.*
import com.menna.nabatan_7asena.core.util.*
import kotlin.test.*

class PrayerSettingsTest {
    private val cairo = PrayerPlace("القاهرة", "EG", 30.0444, 31.2357, "Africa/Cairo")
    private val makkah = PrayerPlace("مكة", "SA", 21.4225, 39.8262, "Asia/Riyadh")
    private class Memory : PrayerSettingsStorage {
        var saved: String? = null
        var fail = false
        override fun read() = saved
        override fun write(value: String) { check(!fail); saved = value }
    }
    @Test fun absentLocationNeverInventsCairoAndAutomaticMethodsFollowTheCountry() {
        assertNull(PrayerSettings().request)
        assertEquals(PrayerCalculationMethod.EGYPTIAN, PrayerCalculationMethod.forCountry("EG"))
        assertEquals(PrayerCalculationMethod.UMM_AL_QURA, PrayerCalculationMethod.forCountry("SA"))
        assertEquals(PrayerCalculationMethod.ISNA, PrayerCalculationMethod.forCountry("US"))
        assertEquals(PrayerCalculationMethod.ISNA, PrayerCalculationMethod.forCountry("CA"))
        assertEquals(PrayerCalculationMethod.MWL, PrayerCalculationMethod.forCountry("GB"))
    }
    @Test fun selectedCityOverridesGpsAndAllPreferencesSurviveRestart() {
        val storage = Memory()
        val repo = PrayerSettingsRepository(storage)
        repo.saveGps(cairo)
        repo.selectCity(makkah)
        assertEquals(PrayerCalculationMethod.UMM_AL_QURA, repo.state.value.request?.method)
        repo.setMethod(PrayerCalculationMethod.MWL)
        repo.setHijriOffset(2)
        val restored = PrayerSettingsRepository(storage)
        assertEquals(repo.state.value, restored.state.value)
        restored.selectCity(null)
        assertEquals(cairo, restored.state.value.place)
        restored.setMethod(null)
        assertEquals(PrayerCalculationMethod.EGYPTIAN, restored.state.value.request?.method)
    }
    @Test fun cacheSignaturesSeparateCoordinatesTimezoneAndMethod() {
        val request = PrayerRequest(cairo, PrayerCalculationMethod.EGYPTIAN)
        assertNotEquals(request.signature, request.copy(place = cairo.copy(latitude = 30.1)).signature)
        assertNotEquals(request.signature, request.copy(place = cairo.copy(timeZoneId = "UTC")).signature)
        assertNotEquals(request.signature, request.copy(method = PrayerCalculationMethod.MWL).signature)
    }
    @Test fun invalidCoordinatesTimezoneAndOffsetAreRejected() {
        assertFailsWith<IllegalArgumentException> { cairo.copy(latitude = Double.NaN) }
        assertFailsWith<IllegalArgumentException> { cairo.copy(longitude = 181.0) }
        assertFailsWith<IllegalArgumentException> { cairo.copy(timeZoneId = "Unknown/Nowhere") }
        assertFailsWith<IllegalArgumentException> { PrayerSettings(hijriDateOffset = 3) }
        assertFailsWith<IllegalArgumentException> { PrayerSettings(hijriDateOffset = -3) }
    }
    @Test fun failedSaveDoesNotPublishNewPreferencesAndGpsJitterDoesNotInvalidateTimes() {
        val storage = Memory()
        val repo = PrayerSettingsRepository(storage)
        repo.saveGps(cairo)
        val saved = storage.saved
        repo.saveGps(cairo.copy(latitude = cairo.latitude + 0.0001))
        assertEquals(saved, storage.saved)
        storage.fail = true
        assertFailsWith<IllegalStateException> { repo.selectCity(makkah) }
        assertEquals(cairo, repo.state.value.place)
    }
    @Test fun seasonsAndRamadanDayUseOneAdjustedDateAcrossBoundaries() {
        assertEquals(SeasonalCalendar(IslamicSeason.RAMADAN, 30), SeasonalThemeManager.forDate(HijriDate(30, 9, 1448)))
        assertEquals(SeasonalCalendar(IslamicSeason.EID_AL_FITR, null), SeasonalThemeManager.forDate(HijriDate(1, 10, 1448)))
        assertEquals(IslamicSeason.DHUL_HIJJAH, SeasonalThemeManager.forDate(HijriDate(8, 12, 1448)).season)
        assertEquals(IslamicSeason.ARAFAH, SeasonalThemeManager.forDate(HijriDate(9, 12, 1448)).season)
        assertEquals(IslamicSeason.EID_AL_ADHA, SeasonalThemeManager.forDate(HijriDate(10, 12, 1448)).season)
        assertEquals(IslamicSeason.ORDINARY, SeasonalThemeManager.forDate(HijriDate(1, 1, 1449)).season)
    }
}
