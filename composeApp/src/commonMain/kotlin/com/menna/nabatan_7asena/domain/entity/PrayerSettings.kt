package com.menna.nabatan_7asena.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PrayerSettings(
    val savedGps: PrayerPlace? = null,
    val selectedCity: PrayerPlace? = null,
    val method: PrayerCalculationMethod? = null,
    val hijriDateOffset: Int = 0
) {
    init { require(hijriDateOffset in -2..2) }
    val place get() = selectedCity ?: savedGps
    val request get() = place?.let { PrayerRequest(it, method ?: PrayerCalculationMethod.forCountry(it.countryCode)) }
}
