package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranSurahIndex(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val downloadStates = state.downloadStates
    val pagerState = layout.pagerState
    LazyColumn(contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)) {
        items(QuranConstants.surahs) { surah ->
            val isSelected = pagerState.currentPage == surah.id - 1
            val dlState = downloadStates[surah.id] ?: SurahDownloadState()
            QuranSurahIndexItem(surah, isSelected, dlState, actions, layout)
        }
    }
}
