package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun PodiumView(users: List<LeaderboardEntry>, currentUserId: String) {
    val first = users.firstOrNull()
    val second = users.getOrNull(1)
    val third = users.getOrNull(2)

    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        if (second != null) {
            PodiumMember(
                user = second, rank = 2, height = 130.dp,
                baseColor = SilverBase,
                darkerColor = SilverDark,
                badgeEmoji = "🥈", isWinner = false,
                isCurrentUser = second.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
        if (first != null) {
            PodiumMember(
                user = first, rank = 1, height = 175.dp,
                baseColor = SummerTheme.colors.goldWarm,
                darkerColor = SummerTheme.colors.goldDeep,
                badgeEmoji = "👑", isWinner = true,
                isCurrentUser = first.userId == currentUserId,
                modifier = Modifier.weight(1.2f).zIndex(2f).offset(y = (-10).dp)
            )
        }
        if (third != null) {
            PodiumMember(
                user = third, rank = 3, height = 100.dp,
                baseColor = BronzeBase,
                darkerColor = BronzeDark,
                badgeEmoji = "🥉", isWinner = false,
                isCurrentUser = third.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
    }
}
