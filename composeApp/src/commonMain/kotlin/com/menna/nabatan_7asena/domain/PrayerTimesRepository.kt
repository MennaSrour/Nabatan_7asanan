package com.menna.nabatan_7asena.domain

import com.menna.nabatan_7asena.domain.entity.PrayerTimes

interface PrayerTimesRepository {
    suspend fun getPrayerTimes(request: com.menna.nabatan_7asena.domain.entity.PrayerRequest, date: String): PrayerTimes =
        getExactPrayerTimes(request.place.city, request.place.countryCode, date)
    suspend fun getPrayerTimes(city: String, country: String, date: String): PrayerTimes
    suspend fun getExactPrayerTimes(city: String, country: String, date: String): PrayerTimes
}