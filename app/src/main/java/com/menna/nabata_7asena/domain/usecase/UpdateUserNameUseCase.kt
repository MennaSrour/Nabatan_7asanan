package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(private val repo: UserRepository) {
    suspend operator fun invoke(name: String): Resource<Unit> {
        return try {
            repo.updateUserName(name)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}