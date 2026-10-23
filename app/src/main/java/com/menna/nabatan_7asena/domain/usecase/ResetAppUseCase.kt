package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.FirstLaunchRepository
import com.menna.nabatan_7asena.domain.repository.UserRepository
import javax.inject.Inject

class ResetAppUseCase @Inject constructor(
    private val repo: UserRepository,
    private val firstLaunchManager: FirstLaunchRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            
            repo.resetAllProgress()

            
            firstLaunchManager.resetToFirstLaunch()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}