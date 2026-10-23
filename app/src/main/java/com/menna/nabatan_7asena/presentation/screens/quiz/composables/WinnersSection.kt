package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun WinnersSection(winners: List<QuizWinner>) {
    val panelShape = RoundedCornerShape(30.dp)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StickerLabel(genderedStringResource(GenderedStrings.quizLastWinners), color = Candy.Orange, rotation = -2f)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(panelShape)
                .background(SummerTheme.colors.cardBackground.copy(alpha = 0.55f))
                .border(2.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.35f), panelShape)
                .padding(top = 34.dp, start = 8.dp, end = 8.dp)
        ) {
            WinnersPodium(winners)
        }
    }
}
