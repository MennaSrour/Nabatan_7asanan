package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

import androidx.compose.foundation.pager.HorizontalPager
import com.menna.nabatan_7asena.domain.entity.AyahModel

@Composable
fun QuranMushafPager(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState, padding: PaddingValues) {
    val navigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val clearance = (76.dp + navigationInset - padding.calculateBottomPadding()).coerceAtLeast(0.dp)
    HorizontalPager(state = layout.mushafPagerState, beyondViewportPageCount = 1,
        modifier = Modifier.padding(padding).padding(bottom = clearance).fillMaxSize()) { index ->
        MushafPage(index + 1, state.mushafPages[index + 1].orEmpty(), state, actions)
    }
}

@Composable
private fun MushafPage(page: Int, ayahs: List<AyahModel>, state: QuranUiState, actions: QuranActions) {
    val q = SummerTheme.quran
    Surface(color = q.headerEnd, shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, q.border), modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 6.dp)) {
        Column(Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
            MushafPageActions(ayahs, state, actions)
            MushafPageBody(ayahs, audioAyah(state.audioState), actions, Modifier.weight(1f))
            HorizontalDivider(color = q.border)
            MushafPageFooter(page)
        }
    }
}

@Composable
private fun MushafPageActions(ayahs: List<AyahModel>, state: QuranUiState, actions: QuranActions) {
    val saved = state.bookmarkedSurahId to state.bookmarkedAyahNo
    val target = mushafBookmarkTarget(ayahs, audioAyah(state.audioState), saved) ?: return
    val marked = ayahs.any { (it.suraNo to it.ayaNo) == saved }
    val purple = SummerTheme.quran.accentStrong
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
        IconButton(modifier = Modifier.size(40.dp), onClick = { actions.downloadSurah(target.suraNo) }) {
            Icon(Icons.Rounded.Download, stringResource(R.string.quran_download_surah), tint = purple)
        }
        IconButton(modifier = Modifier.size(40.dp), onClick = { actions.onOpenRepeatBottomSheet(target.suraNo) }) {
            Icon(Icons.Rounded.Repeat, genderedStringResource(GenderedStrings.quranRepeatMemorize), tint = purple)
        }
        IconButton(modifier = Modifier.size(40.dp), onClick = { actions.saveBookmark(target.suraNo, target.ayaNo) }) {
            Icon(if (marked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                stringResource(R.string.quran_bookmark_page, arabicIndic(target.ayaNo)), tint = purple)
        }
    }
}

@Composable
private fun MushafPageFooter(page: Int) {
    Row(Modifier.fillMaxWidth().heightIn(min = 26.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.quran_page_label, arabicIndic(page)), color = SummerTheme.quran.accentStrong)

    }
}

fun audioAyah(audio: QuranAudioState): Pair<Int, Int>? = when (audio) {
    is QuranAudioState.Playing -> audio.suraNo to audio.ayaNo
    is QuranAudioState.Loading -> audio.suraNo to audio.ayaNo
    is QuranAudioState.Paused -> audio.suraNo to audio.ayaNo
    else -> null
}
