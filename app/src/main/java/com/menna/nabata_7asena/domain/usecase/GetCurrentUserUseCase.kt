package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User> {
        return repository.getCurrentUser()
    }
}