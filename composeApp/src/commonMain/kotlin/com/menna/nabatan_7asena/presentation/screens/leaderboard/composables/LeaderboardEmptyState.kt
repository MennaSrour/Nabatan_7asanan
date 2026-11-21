package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun LeaderboardEmptyState() {
    val floatOffset = SummerTheme.rememberFloatingAnimation()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🌟",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                genderedStringResource(GenderedStrings.leaderboardFirstStar),
                fontSize = 22.sp,
                color = SummerTheme.colors.textPrimary,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                genderedStringResource(GenderedStrings.leaderboardEarnStars),
                fontSize = 14.sp,
                color = SummerTheme.colors.textBrown
            )
        }
    }
}
