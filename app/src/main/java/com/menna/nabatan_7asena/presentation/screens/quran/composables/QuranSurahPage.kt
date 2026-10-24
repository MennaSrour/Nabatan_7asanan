package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranSurahPage(pageIndex: Int, state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val downloadStates = state.downloadStates
    val currentPageNumber = state.currentPageNumber
    val quranFont = FontFamily(Font(R.font.amiri_regular))
    val lazyListStates = layout.lazyListStates
    val currentSurahId = pageIndex + 1
    val surahInfo = QuranConstants.surahs[currentSurahId - 1]
    val ayahs = state.ayahsBySurah[currentSurahId].orEmpty()

    LaunchedEffect(currentSurahId) {
        actions.onSurahVisible(currentSurahId)
    }
    val surahDownloadState = downloadStates[currentSurahId]
    ?: SurahDownloadState(totalAyahs = ayahs.size)

    val lazyListState = rememberLazyListState()
    SideEffect { lazyListStates[pageIndex] = lazyListState }

    Column(modifier = Modifier.fillMaxSize()) {
        SurahHeaderCard(
            surah = surahInfo,
            ayahsCount = ayahs.size,
            currentPage = currentPageNumber,
            downloadState = surahDownloadState,
            onDownloadClick = { actions.downloadSurah(currentSurahId) },
            onRepeatClick = { actions.onOpenRepeatBottomSheet(currentSurahId) }
        )

        QuranAyahList(ayahs, state, actions, lazyListState, quranFont, currentSurahId)
    }
}
