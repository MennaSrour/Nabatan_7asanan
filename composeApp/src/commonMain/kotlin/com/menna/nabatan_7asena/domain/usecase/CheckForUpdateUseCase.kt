package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.entity.UpdateInfo
import com.menna.nabatan_7asena.domain.repository.UpdateRepository

class CheckForUpdateUseCase(
    private val repository: UpdateRepository
) {
    suspend operator fun invoke(): UpdateInfo {
        return repository.checkForUpdates()
    }
}