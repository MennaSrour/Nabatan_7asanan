package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LazyItemScope.HomeTaskFeedItem(task: HomeUiState.UiTaskItem, hasBookmark: Boolean, actions: HomeActions) {
    Box(Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)
            .animateItem(placementSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy))) {
        TaskBubbleCard(task, hasBookmark, onClick = { actions.onTaskClicked(task) },
            onCheckClick = { actions.onTaskChecked(task) }, onPlaySound = { actions.playTaskSound(task) })
    }
}
