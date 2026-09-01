package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuranRepository
import javax.inject.Inject

class SaveQuranProgressUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(page: Int) = repository.saveQuranPage(page)
}