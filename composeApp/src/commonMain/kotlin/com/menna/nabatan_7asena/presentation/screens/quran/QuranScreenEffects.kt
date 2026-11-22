package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.resources.AppResources as R

@Composable
fun QuranScreenEffects(state: QuranUiState, layout: QuranLayoutState, viewModel: QuranViewModel, wirdPage: Int?) {
    val savedMessage = genderedStringResource(GenderedStrings.quranBookmarkSaved)
    val errorMessage = genderedStringResource(GenderedStrings.unexpectedError)
    val playbackError = genderedStringResource(GenderedStrings.quranPlaybackError)
    val libraryError = stringResource(R.string.quran_library_error)
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            QuranEffect.JourneySaveFailed -> layout.snackbar.showSnackbar("تعذّر حفظ رحلتك مع القرآن. حاوِل مرة أخرى.")
            QuranEffect.LibrarySaveFailed -> layout.snackbar.showSnackbar(libraryError)
            QuranEffect.PlaybackFailed -> layout.snackbar.showSnackbar(playbackError)
            QuranEffect.LoadFailed -> layout.snackbar.showSnackbar(errorMessage)
            QuranEffect.BookmarkSaved -> layout.snackbar.showSnackbar(savedMessage)
            is QuranEffect.ScrollToAyah -> {
                layout.navigationJob?.cancel()
                layout.navigationJob = layout.scope.launch {
                    if (state.viewMode == QuranViewMode.MUSHAF) layout.mushafPagerState.scrollToPage((effect.page - 1).coerceIn(0, 603))
                    else scrollToAyah(layout, effect)
                }
            }
        }
    }
    LaunchedEffect(state.viewMode, state.isReaderReady, wirdPage) {
        layout.navigationJob?.cancel()
        if (!state.isReaderReady) return@LaunchedEffect
        if (state.viewMode == QuranViewMode.MUSHAF) {
            val page = wirdPage ?: viewModel.readerPage()
            layout.mushafPagerState.scrollToPage((page - 1).coerceIn(0, 603))
            if (wirdPage != null) viewModel.onWirdPageOpened(wirdPage)
            snapshotFlow { layout.mushafPagerState.settledPage }.distinctUntilChanged().collect {
                viewModel.onMushafPageChanged(it + 1)
            }
        } else {
            val first = state.mushafPages[state.currentPageNumber]?.firstOrNull()
            if (first != null) scrollToAyah(layout, QuranEffect.ScrollToAyah(first.suraNo - 1, first.ayaNo, first.page))
            snapshotFlow { layout.pagerState.settledPage }.distinctUntilChanged().collectLatest { index ->
                viewModel.onSurahChanged(index + 1)
                snapshotFlow { layout.lazyListStates[index]?.firstVisibleItemIndex }.filterNotNull()
                    .distinctUntilChanged().collect { viewModel.onVisibleAyahChanged(index + 1, it) }
            }
        }
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
