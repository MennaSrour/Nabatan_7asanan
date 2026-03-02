package com.menna.nabata_7asena.domain

import com.menna.nabata_7asena.domain.entity.PrayerTimes

interface PrayerTimesRepository {
    suspend fun getPrayerTimes(city: String, country: String, date: String): PrayerTimes
}