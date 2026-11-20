package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuranRepository

class SaveQuranProgressUseCase constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(page: Int) = repository.saveQuranPage(page)
}