package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.UserRepository

class ToggleNotificationsUseCase constructor(private val repo: UserRepository) {

    suspend operator fun invoke(enabled: Boolean): Resource<Unit> {
        return try {
            repo.setNotificationsEnabled(enabled)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}