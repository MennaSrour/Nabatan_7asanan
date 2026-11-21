package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerStreakCard(streak: Int) {
    val colors = SummerTheme.colors
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        colors.goldDeep,
                        colors.goldWarm,
                        colors.goldWarm
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(colors.white.copy(0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("⚡", fontSize = 28.sp) }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(genderedStringResource(GenderedStrings.badgesStreakTitle), fontWeight = FontWeight.Bold, color = colors.textBrown, fontSize = 13.sp)
                        Text(genderedStringResource(GenderedStrings.streakDays, streak), fontSize = 28.sp, fontWeight = FontWeight.Black, color = colors.textDarkBrown)
                    }
                }

                Surface(
                    color = colors.white.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        genderedStringResource(GenderedStrings.badgesStreakHint),
                        fontSize = 12.sp,
                        color = colors.textDarkBrown,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
