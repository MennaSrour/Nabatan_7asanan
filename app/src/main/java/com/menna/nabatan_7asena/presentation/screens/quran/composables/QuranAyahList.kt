package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranAudioState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ColumnScope.QuranAyahList(ayahs: List<com.menna.nabatan_7asena.domain.entity.AyahModel>, state: QuranUiState, actions: QuranActions, lazyListState: LazyListState, quranFont: FontFamily, currentSurahId: Int) {
    val bookmarkedSurahId = state.bookmarkedSurahId
    val bookmarkedAyahNo = state.bookmarkedAyahNo
    val audioState = state.audioState
    LazyColumn(
        state       = lazyListState,
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top   = 8.dp,  bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f).fillMaxWidth()
    ) {
        val hasBasmala = currentSurahId != 1 && currentSurahId != 9
        if (hasBasmala) {
            item { BasmalaCard(quranFont) }
        }

        itemsIndexed(ayahs) { _, ayah ->
            val isBookmarked = bookmarkedSurahId == ayah.suraNo &&
            bookmarkedAyahNo  == ayah.ayaNo

            val ayahAudioState: AyahAudioState = when (val s = audioState) {
                is QuranAudioState.Loading ->
                if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                AyahAudioState.LOADING else AyahAudioState.IDLE
                is QuranAudioState.Playing ->
                if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                AyahAudioState.PLAYING else AyahAudioState.IDLE
                is QuranAudioState.Paused ->
                if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                AyahAudioState.PAUSED else AyahAudioState.IDLE
                else -> AyahAudioState.IDLE
            }

            AyahCard(
                ayah            = ayah,
                isBookmarked    = isBookmarked,
                quranFont       = quranFont,
                audioState      = ayahAudioState,
                onPlayAudio     = { actions.playAyahAudio(ayah.suraNo, ayah.ayaNo) },
                onBookmarkClick = {
                    actions.saveBookmark(ayah.suraNo, ayah.ayaNo)
                }
            )
        }
    }
}
