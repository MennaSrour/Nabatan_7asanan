package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
fun LeaderboardHeader() {
    val floatOffset = SummerTheme.rememberFloatingAnimation()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🏆",
                fontSize = 40.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                genderedStringResource(GenderedStrings.leaderboardTitle),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = SummerTheme.colors.textPrimary
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                color = SummerTheme.colors.goldWarm.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        genderedStringResource(GenderedStrings.leaderboardSubtitle),
                        fontSize = 13.sp,
                        color = SummerTheme.colors.goldDeep,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
