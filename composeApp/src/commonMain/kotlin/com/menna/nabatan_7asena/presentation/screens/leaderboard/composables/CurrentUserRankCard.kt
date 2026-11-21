package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.leaderboard.LeaderboardUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun CurrentUserRankCard(state: LeaderboardUiState, modifier: Modifier = Modifier) {
    val user = state.currentUser ?: return
    Surface(modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = SummerTheme.Shapes.MediumRounded,
        color = SummerTheme.colors.dialogBackground, shadowElevation = 6.dp,
        border = BorderStroke(1.5.dp, SummerTheme.colors.goldWarm.copy(0.7f))) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(if (state.isRankUnavailable) R.string.leaderboard_rank_unavailable else R.string.leaderboard_your_rank),
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SummerTheme.colors.textBrown)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LeaderboardRankBadge(state.currentUserRank?.toString() ?: "—", state.isRankLoading)
                LeaderboardMemberDetails(user.name, user.gender, user.avatarIndex, user.totalStars,
                    user.quranPartsFinished, isCurrentUser = true, modifier = Modifier.weight(1f))
            }
        }
    }
}
