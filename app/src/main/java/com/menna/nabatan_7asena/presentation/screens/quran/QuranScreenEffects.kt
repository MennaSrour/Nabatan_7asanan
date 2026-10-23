package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
internal fun QuranScreenEffects(state: QuranUiState, layout: QuranLayoutState, viewModel: QuranViewModel) {
    val savedMessage = genderedStringResource(GenderedStrings.quranBookmarkSaved)
    val errorMessage = genderedStringResource(GenderedStrings.unexpectedError)
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            QuranEffect.LoadFailed -> layout.snackbar.showSnackbar(errorMessage)
            QuranEffect.BookmarkSaved -> layout.snackbar.showSnackbar(savedMessage)
            is QuranEffect.ScrollToAyah -> scrollToAyah(layout, effect)
        }
    }
    LaunchedEffect(layout.pagerState.currentPage) { viewModel.onSurahChanged(layout.pagerState.currentPage + 1) }
    LaunchedEffect(layout, state.ayahsBySurah) {
        snapshotFlow { layout.pagerState.currentPage to layout.lazyListStates[layout.pagerState.currentPage]?.firstVisibleItemIndex }
            .collect { (page, index) -> index?.let { viewModel.onVisibleAyahChanged(page + 1, it) } }
    }
}

private suspend fun scrollToAyah(layout: QuranLayoutState, target: QuranEffect.ScrollToAyah) {
    layout.pagerState.scrollToPage(target.surahIndex)
    val list = snapshotFlow { layout.lazyListStates[target.surahIndex] }.filterNotNull().first()
    val hasBasmala = target.surahIndex + 1 !in listOf(1, 9)
    val index = (if (hasBasmala) target.ayaNo else target.ayaNo - 1).coerceAtLeast(0)
    snapshotFlow { list.layoutInfo.totalItemsCount }.first { it > index }
    list.scrollToItem(index)
}
