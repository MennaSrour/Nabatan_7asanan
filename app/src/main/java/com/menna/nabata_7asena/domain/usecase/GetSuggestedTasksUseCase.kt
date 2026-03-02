package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import javax.inject.Inject

class GetSuggestedTasksUseCase @Inject constructor(
    private val repository: DailyActivityRepository
) {
    
    suspend operator fun invoke(): Resource<ExtraTasks> {
        return try {
            val tasks = repository.getSuggestedExtraTasks()
            Resource.Success(tasks)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}