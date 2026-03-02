package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.data.local.FirstLaunchManager
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.repository.UserRepository
import javax.inject.Inject

class ResetAppUseCase @Inject constructor(
    private val repo: UserRepository,
    private val firstLaunchManager: FirstLaunchManager 
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