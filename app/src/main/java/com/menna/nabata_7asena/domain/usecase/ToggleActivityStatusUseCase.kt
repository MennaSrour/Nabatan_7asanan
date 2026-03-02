package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import com.menna.nabata_7asena.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleActivityStatusUseCase @Inject constructor(
    private val dailyRepository: DailyActivityRepository,
    private val userRepository: UserRepository 
) {
    suspend operator fun invoke(date: String, activityId: Int, isCompleted: Boolean): Resource<Unit> {
        return try {
            
            dailyRepository.updateActivityStatus(date, activityId, isCompleted)

            
            try {
                
                val updatedUser = userRepository.getCurrentUser().first()

                
                userRepository.syncUserScore(updatedUser)
            } catch (e: Exception) {
                
                e.printStackTrace()
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}