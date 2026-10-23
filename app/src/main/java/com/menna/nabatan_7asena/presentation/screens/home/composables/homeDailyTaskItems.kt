package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyListScope.homeDailyTaskItems(state: HomeUiState, actions: HomeActions, tint: Color) {
    val tasks = state.tasks.filter { it.category != TaskCategory.PRAYER }
    item { HomeSectionHeader(R.drawable.todo, genderedStringResource(GenderedStrings.homeDailyTasks), tint) }
    if (tasks.isEmpty()) item { EmptyDailyTasksHint(tint) }
    else items(tasks, key = { it.id }) { task -> HomeTaskFeedItem(task, state.hasBookmark, actions) }
}
