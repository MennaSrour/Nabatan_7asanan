package com.menna.nabata_7asena.data.repository

import com.menna.nabata_7asena.data.PrayerTimesApi
import com.menna.nabata_7asena.data.toEntity
import com.menna.nabata_7asena.domain.PrayerTimesRepository
import com.menna.nabata_7asena.domain.entity.PrayerTimes
import javax.inject.Inject

class PrayerTimesRepositoryImpl @Inject constructor(
    private val api: PrayerTimesApi
) : PrayerTimesRepository {
    override suspend fun getPrayerTimes(
        city: String,
        country: String,
        date: String
    ): PrayerTimes = api.getPrayerTimesByCity(
        city = city,
        country = country,
        date = date
    ).toEntity()
}