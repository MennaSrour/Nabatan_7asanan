package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow

class GetQuranProgressUseCase constructor(
    private val repository: QuranRepository
) {
    fun getLastPage(): Flow<Int> = repository.getLastQuranPage()
}