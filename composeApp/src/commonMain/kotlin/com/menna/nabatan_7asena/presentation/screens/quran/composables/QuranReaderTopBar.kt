package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderTopBar(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    CenterAlignedTopAppBar(
        title = { QuranReaderTitle(state) },
        navigationIcon = {
            Row {
                IconButton(onClick = { layout.scope.launch { layout.drawerState.open() } }, enabled = state.isReaderReady) {
                    Icon(Icons.Rounded.Menu, genderedStringResource(GenderedStrings.quranSurahs))
                }
                if (state.bookmarkedSurahId > 0) IconButton(onClick = actions::goToBookmark) {
                    Icon(Icons.Rounded.Bookmark, stringResource(R.string.quran_bookmark_page, arabicIndic(state.bookmarkedAyahNo)))
                }
            }
        },
        actions = { QuranViewActions(state, actions) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SummerTheme.quran.topBar))
}

@Composable
private fun QuranViewActions(state: QuranUiState, actions: QuranActions) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = actions::toggleViewMode, enabled = state.isReaderReady) {
            Icon(if (state.viewMode == QuranViewMode.CARDS) Icons.Rounded.MenuBook else Icons.Rounded.ViewAgenda,
                stringResource(if (state.viewMode == QuranViewMode.CARDS) R.string.quran_view_mushaf else R.string.quran_view_cards),
                tint = SummerTheme.quran.accentStrong)
        }
        if (state.audioState !is QuranAudioState.Idle) IconButton(onClick = actions::stopAudio) {
            Icon(Icons.Rounded.Stop, genderedStringResource(GenderedStrings.quranStopPlayback), tint = SummerTheme.quran.accentStrong)
        }
    }
}

@Composable
private fun QuranReaderTitle(state: QuranUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(genderedStringResource(GenderedStrings.quranActiveJuz, formatJuzNumber(state.activeJuz)),
            fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SummerTheme.quran.accentStrong,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (state.viewMode == QuranViewMode.MUSHAF) {
            val ayahs = state.mushafPages[state.currentPageNumber].orEmpty()
            val activeSurah = audioAyah(state.audioState)?.first
            val surah = ayahs.firstOrNull { it.suraNo == activeSurah } ?: ayahs.firstOrNull()
            surah?.let {
                Text(it.suraNameAr, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = SummerTheme.quran.accentStrong, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            return@Column
        }
        val status = when (state.audioState) {
            is QuranAudioState.Loading -> GenderedStrings.loading
            is QuranAudioState.Playing -> GenderedStrings.quranPlaying
            is QuranAudioState.Paused -> GenderedStrings.quranPaused
            else -> null
        }
        status?.let { Text(genderedStringResource(it), fontSize = 11.sp, color = SummerTheme.quran.accent) }
    }
}
