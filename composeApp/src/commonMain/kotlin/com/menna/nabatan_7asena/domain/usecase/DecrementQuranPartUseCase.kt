package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.UserRepository

class DecrementQuranPartUseCase constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.decrementQuranPart()
    }
}