package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.entity.Activity
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyActivitiesUseCase @Inject constructor(
    private val repository: DailyActivityRepository
) {
    operator fun invoke(date: String, city: String,country:String): Flow<List<Activity>> {
        return repository.getDailyActivities(date, city,country)
    }

    
    
    suspend fun getTodayRiddle(): DailyContent? {
        return repository.getTodayRiddle()
    }
}