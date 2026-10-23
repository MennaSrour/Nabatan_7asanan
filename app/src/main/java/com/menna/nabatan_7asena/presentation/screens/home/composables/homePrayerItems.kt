package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.components.NoInternetState
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyListScope.homePrayerItems(state: HomeUiState, actions: HomeActions, tint: Color) {
    item { HomeSectionHeader(R.drawable.time, genderedStringResource(GenderedStrings.homePrayers), tint) }
    item {
        if (state.hasPrayerLoadError) NoInternetState(
            title = genderedStringResource(GenderedStrings.homePrayerError), onRetry = actions::retryLoadingData,
            compact = true, modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal)
        ) else PrayerTimesRow(state.tasks.filter { it.category == TaskCategory.PRAYER }, actions::onTaskChecked, Modifier.padding(vertical = 6.dp))
    }
}
