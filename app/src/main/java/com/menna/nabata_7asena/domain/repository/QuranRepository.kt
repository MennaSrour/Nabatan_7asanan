package com.menna.nabata_7asena.domain.repository

import com.menna.nabata_7asena.domain.entity.AyahModel
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun getLastQuranPage(): Flow<Int>
    suspend fun saveQuranPage(page: Int)
    fun getPageText(pageNumber: Int): String
    fun getSurahAyahs(suraNumber: Int): List<AyahModel>
    fun getBookmarkStatusFlow(): Flow<Boolean>
}