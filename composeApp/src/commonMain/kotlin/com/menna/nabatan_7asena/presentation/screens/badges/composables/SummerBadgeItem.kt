package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerBadgeItem(badge: Achievement) {
    val colors = SummerTheme.colors
    Card(
        modifier = Modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(if (badge.isUnlocked) 6.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                if (badge.isUnlocked)
                Brush.verticalGradient(
                    listOf(
                        badge.color.copy(alpha = 0.2f),
                        colors.white.copy(alpha = 0.95f)
                    )
                )
                else
                Brush.verticalGradient(
                    listOf(colors.chipUnselected, colors.dayBackground)
                )
            )
                .border(
                width = if (badge.isUnlocked) 2.dp else 1.dp,
                color = if (badge.isUnlocked) badge.color.copy(0.8f) else colors.textBrown.copy(0.2f),
                shape = RoundedCornerShape(20.dp)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                        if (badge.isUnlocked)
                        badge.color.copy(alpha = 0.2f)
                        else
                        colors.white.copy(alpha = 0.4f)
                    )
                        .border(
                        2.dp,
                        if (badge.isUnlocked) badge.color.copy(0.7f) else colors.textBrown.copy(0.2f),
                        CircleShape
                    )
                ) {
                    if (badge.isUnlocked)
                    Text(badge.icon, fontSize = 30.sp)
                    else
                    Text("🔒", fontSize = 26.sp, modifier = Modifier.alpha(0.3f))
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    badge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (badge.isUnlocked) colors.textPrimary else colors.textBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    badge.description,
                    fontSize = 10.sp,
                    color = if (badge.isUnlocked) colors.textBrown else colors.textBrown.copy(0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
