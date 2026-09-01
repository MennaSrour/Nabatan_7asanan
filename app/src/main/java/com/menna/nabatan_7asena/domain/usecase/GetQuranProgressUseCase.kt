package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuranProgressUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    fun getLastPage(): Flow<Int> = repository.getLastQuranPage()
}