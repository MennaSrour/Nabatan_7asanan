package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranJuzIndex(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val activeJuz = state.activeJuz
    LazyColumn(contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)) {
        items(30) { i ->
            val juz = i + 1
            val isSelected = activeJuz == juz
            QuranJuzIndexItem(juz, isSelected, actions, layout)
        }
    }
}
