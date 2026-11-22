package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.DailyActivityRepository
import kotlinx.coroutines.CancellationException

class ToggleActivityStatusUseCase(private val dailyRepository: DailyActivityRepository) {
    suspend operator fun invoke(date: String, activityId: Int, isCompleted: Boolean): Resource<Unit> = try {
        dailyRepository.updateActivityStatus(date, activityId, isCompleted)
        Resource.Success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resource.Error(e)
    }
}
