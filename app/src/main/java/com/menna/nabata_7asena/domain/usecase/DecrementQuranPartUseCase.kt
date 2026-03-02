package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.repository.UserRepository
import javax.inject.Inject

class DecrementQuranPartUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.decrementQuranPart()
    }
}