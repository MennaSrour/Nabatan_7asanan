package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTreasureSummary(state: HomeUiState, onOpen: () -> Unit) {
    val prayers = state.tasks.filter { it.category == TaskCategory.PRAYER }
    val tasks = state.tasks.filter { it.category != TaskCategory.PRAYER }
    val unlocked = tasks.isNotEmpty() && prayers.isNotEmpty() && state.tasks.all { it.isCompleted }
    Box(Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 12.dp)) {
        TreasureChestCard(isUnlocked = unlocked, completedCount = state.tasks.count { it.isCompleted }, totalCount = state.tasks.size, onClick = onOpen)
    }
}
