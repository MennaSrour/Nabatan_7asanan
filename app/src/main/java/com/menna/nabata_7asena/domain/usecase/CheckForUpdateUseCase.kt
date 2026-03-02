package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.entity.UpdateInfo
import com.menna.nabata_7asena.domain.repository.UpdateRepository

class CheckForUpdateUseCase(
    private val repository: UpdateRepository
) {
    suspend operator fun invoke(): UpdateInfo {
        return repository.checkForUpdates()
    }
}