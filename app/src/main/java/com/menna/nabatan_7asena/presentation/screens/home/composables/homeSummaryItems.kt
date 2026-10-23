package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyListScope.homeSummaryItems(state: HomeUiState) {
    item { BentoStatsGrid(state.user, Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)) }
    item { DailyWisdomCard(state.dailyWisdom, Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)) }
}
