package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import javax.inject.Inject

class AddExtraTaskUseCase @Inject constructor(
    private val repository: DailyActivityRepository
) {
    suspend operator fun invoke(title: String): Resource<Unit> {
        return try {
            if (title.isNotBlank()) {
                repository.addExtraTask(title)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}