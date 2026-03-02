package com.menna.nabata_7asena.domain

import android.util.Log
import com.menna.nabata_7asena.domain.entity.PrayerTimes
import javax.inject.Inject

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: PrayerTimesRepository
) {
    suspend operator fun invoke(city: String, country: String, date: String): PrayerTimes? {
        return try {
            repository.getPrayerTimes(city, country, date)
        } catch (e: Exception) {
            Log.e("GetPrayerTimesUseCase", "Error fetching prayer times: ${e.message}")
            null
        }
    }
}