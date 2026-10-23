package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.QuizWinner

@Composable
internal fun WinnersPodium(winners: List<QuizWinner>) {
    val first = winners.getOrNull(0)
    val second = winners.getOrNull(1)
    val third = winners.getOrNull(2)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        second?.let { PodiumSpot(it, rank = 2, medalColor = SilverMedal, medalEmoji = "🥈", height = 92.dp) }
        first?.let { PodiumSpot(it, rank = 1, medalColor = GoldMedal, medalEmoji = "🥇", height = 120.dp) }
        third?.let { PodiumSpot(it, rank = 3, medalColor = BronzeMedal, medalEmoji = "🥉", height = 76.dp) }
    }
}
