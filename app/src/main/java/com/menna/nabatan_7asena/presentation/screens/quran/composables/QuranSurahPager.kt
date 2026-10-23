package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranSurahPager(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState, padding: PaddingValues) {
    val pagerState = layout.pagerState
    HorizontalPager(
        state    = pagerState,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        beyondViewportPageCount = 1
    ) { pageIndex ->
        QuranSurahPage(pageIndex, state, actions, layout)
    }
}
