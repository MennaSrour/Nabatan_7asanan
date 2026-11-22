package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun HomeTreasureSummary(state: HomeUiState, onOpen: (String) -> Unit) {
    val reminder = if (state.dailyGiftRemaining > 0) {
        genderedStringResource(GenderedStrings.treasureRemaining, state.dailyGiftRemaining)
    } else {
        stringResource(R.string.treasure_waiting_tasks)
    }
    Box(Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 12.dp)) {
        TreasureChestCard(
            isUnlocked = state.isDailyGiftUnlocked,
            completedCount = state.dailyGiftTotal - state.dailyGiftRemaining,
            totalCount = state.dailyGiftTotal,
            onClick = { onOpen(reminder) }
        )
    }
}
