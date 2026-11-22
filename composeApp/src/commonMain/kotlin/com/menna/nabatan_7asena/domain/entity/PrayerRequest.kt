package com.menna.nabatan_7asena.domain.entity

import kotlinx.serialization.Serializable
import kotlinx.datetime.TimeZone

@Serializable
data class PrayerPlace(
    val city: String, val countryCode: String,
    val latitude: Double, val longitude: Double, val timeZoneId: String
) {
    init {
        require(city.isNotBlank() && countryCode.length == 2)
        require(latitude.isFinite() && latitude in -90.0..90.0)
        require(longitude.isFinite() && longitude in -180.0..180.0)
        TimeZone.of(timeZoneId)
    }
}

@Serializable
enum class PrayerCalculationMethod(val apiId: Int) {
    EGYPTIAN(5), UMM_AL_QURA(4), MWL(3), ISNA(2);
    companion object {
        fun forCountry(code: String) = when (code.uppercase()) {
            "EG", "EGYPT" -> EGYPTIAN
            "SA", "SAUDI ARABIA" -> UMM_AL_QURA
            "US", "CA", "UNITED STATES", "UNITED STATES OF AMERICA", "CANADA" -> ISNA
            else -> MWL
        }
    }
}

@Serializable
data class PrayerRequest(val place: PrayerPlace, val method: PrayerCalculationMethod) {
    val signature: String get() = "${place.latitude}|${place.longitude}|${place.timeZoneId}|${method.apiId}"
}
