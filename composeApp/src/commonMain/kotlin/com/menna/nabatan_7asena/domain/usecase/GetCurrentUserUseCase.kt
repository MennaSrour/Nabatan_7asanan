package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User> {
        return repository.getCurrentUser()
    }
}