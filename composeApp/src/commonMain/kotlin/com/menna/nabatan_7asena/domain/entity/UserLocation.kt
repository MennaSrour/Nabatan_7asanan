package com.menna.nabatan_7asena.domain.entity

data class UserLocation(
    val city: String,
    val country: String,
    val latitude: Double, 
    val longitude: Double,
    val countryCode: String = "",
    val timeZoneId: String = kotlinx.datetime.TimeZone.currentSystemDefault().id
)
fun UserLocation.toPrayerPlace(): PrayerPlace? = try {
    PrayerPlace(city.ifBlank { country }, countryCode, latitude, longitude, timeZoneId)
} catch (_: IllegalArgumentException) { null }
