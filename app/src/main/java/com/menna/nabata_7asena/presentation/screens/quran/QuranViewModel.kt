package com.menna.nabata_7asena.presentation.screens.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.domain.QuranConstants
import com.menna.nabata_7asena.domain.repository.QuranRepository
import com.menna.nabata_7asena.domain.usecase.GetQuranProgressUseCase
import com.menna.nabata_7asena.domain.usecase.SaveQuranProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val saveUseCase: SaveQuranProgressUseCase,
    private val getUseCase: GetQuranProgressUseCase,
    private val repository: QuranRepository
) : ViewModel() {

    val lastSavedPage = getUseCase.getLastPage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    fun onPageChanged(page: Int) {
        viewModelScope.launch { saveUseCase.invoke(page) }
    }

    fun getPageText(page: Int) = repository.getPageText(page)

    fun getSurahInfo(page: Int) = QuranConstants.getSurahByPage(page)

    fun getStartPageForJuz(juz: Int): Int {
        val juzStartPages = listOf(1, 22, 42, 62, 82, 102, 122, 142, 162, 182, 202, 222, 242, 262, 282, 302, 322, 342, 362, 382, 402, 422, 442, 462, 482, 502, 522, 542, 562, 582)
        return juzStartPages.getOrNull(juz - 1) ?: 1
    }
}