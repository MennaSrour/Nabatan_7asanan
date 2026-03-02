package com.menna.nabata_7asena.domain.repository

import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun getLastQuranPage(): Flow<Int>
    suspend fun saveQuranPage(page: Int)
    fun getPageText(pageNumber: Int): String
}