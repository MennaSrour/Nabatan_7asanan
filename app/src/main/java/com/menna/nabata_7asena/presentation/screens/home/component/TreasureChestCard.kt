package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun TreasureChestCard(
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "chest")
    val bounce by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "bounce"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isUnlocked) {
                    Brush.verticalGradient(listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep))
                } else {
                    Brush.verticalGradient(listOf(SummerTheme.colors.chestLockedStart, SummerTheme.colors.chestLockedEnd))
                }
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(if (isUnlocked) bounce else 1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SummerTheme.colors.white.copy(alpha = if (isUnlocked) 0.25f else 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Rounded.CardGiftcard else Icons.Rounded.Lock,
                contentDescription = null,
                tint = if (isUnlocked) SummerTheme.colors.white else SummerTheme.colors.chestLockedText,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = if (isUnlocked) "الكنز اتفتح" else "كملي مهامك وصلواتك",
            style = SummerTheme.typography.chestTitle,
            color = if (isUnlocked) SummerTheme.colors.white else SummerTheme.colors.chestLockedText,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = if (isUnlocked) "اضغطي وشوفي مفاجأتك" else "عشان تفتحي الكنز",
            style = SummerTheme.typography.chestSubtitle,
            color = if (isUnlocked) SummerTheme.colors.white.copy(alpha = 0.85f) else SummerTheme.colors.chestLockedText.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}