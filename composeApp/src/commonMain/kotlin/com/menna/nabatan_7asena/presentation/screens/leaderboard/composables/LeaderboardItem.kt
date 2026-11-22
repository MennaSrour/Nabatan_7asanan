package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun LeaderboardItem(user: LeaderboardEntry, rank: Int, isCurrentUser: Boolean) {
    val colors = SummerTheme.colors
    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = SummerTheme.Shapes.MediumRounded,
        color = if (isCurrentUser) colors.goldWarm.copy(0.16f) else colors.cardBackground.copy(0.8f),
        border = BorderStroke(1.dp, if (isCurrentUser) colors.highlightAccent.copy(0.5f) else colors.textBrown.copy(0.15f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LeaderboardRankBadge(rank.toString())
            LeaderboardMemberDetails(user.name, user.gender, user.avatarIndex, user.totalStars,
                user.completedKhatmahs, isCurrentUser, Modifier.weight(1f))
        }
    }
}
