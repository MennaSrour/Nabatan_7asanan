package com.menna.nabatan_7asena.domain

import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: PrayerTimesRepository
) {
    suspend operator fun invoke(city: String, country: String, date: String): PrayerTimes? {
        return try {
            repository.getPrayerTimes(city, country, date)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
}