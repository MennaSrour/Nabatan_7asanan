package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun DailyChallengeFeedback(isCorrect: Boolean) {
    Text(
        text = genderedStringResource(if (isCorrect) GenderedStrings.challengeCorrectAnswer else GenderedStrings.challengeWrongAnswer),
        color = if (isCorrect) SummerTheme.colors.successGreen else SummerTheme.colors.errorRed,
        style = SummerTheme.typography.taskTitle,
        modifier = Modifier.padding(vertical = 10.dp).semantics { liveRegion = LiveRegionMode.Polite }
    )
}
