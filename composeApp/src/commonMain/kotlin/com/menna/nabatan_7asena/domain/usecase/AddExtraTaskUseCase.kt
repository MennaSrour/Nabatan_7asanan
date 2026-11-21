package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.DailyActivityRepository

class AddExtraTaskUseCase constructor(
    private val repository: DailyActivityRepository
) {
    suspend operator fun invoke(title: String, existingTitles: List<String> = emptyList()): Resource<Unit> {
        return try {
            com.menna.nabatan_7asena.domain.util.requireNewDailyTask(title, existingTitles)
            repository.addExtraTask(title.trim())
            Resource.Success(Unit)
        } catch (e: kotlinx.coroutines.CancellationException) { throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}