package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource

@Composable
internal fun StudyEntryCard(onClick: () -> Unit) {
    ChunkyButton(
        onClick = onClick,
        color = Candy.Leaf,
        edgeColor = Candy.LeafEdge,
        onClickLabel = genderedStringResource(GenderedStrings.quizStudyAction),
        modifier = Modifier
            .fillMaxWidth()
            .rotate(-1.2f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
    ) {
        EntryCardContent(
            emoji = "📖",
            title = genderedStringResource(GenderedStrings.quizStudyAction),
            subtitle = genderedStringResource(GenderedStrings.quizStudyHint),
            titleColor = Color.White,
            subtitleColor = Color.White.copy(alpha = 0.92f)
        )
    }
}
