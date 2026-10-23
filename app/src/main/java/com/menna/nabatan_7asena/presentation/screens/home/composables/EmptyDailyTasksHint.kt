package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun EmptyDailyTasksHint(tint: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genderedStringResource(GenderedStrings.homeNoTasks),
            style = SummerTheme.typography.taskSubtitle,
            color = tint.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
    }
}
