package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun FloatingNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) item.color.copy(alpha = 0.16f) else Color.Transparent,
        label = "bgColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) item.color
        else SummerTheme.colors.textBrown.copy(alpha = 0.55f),
        label = "iconColor"
    )

    val assetName = when (item.route) {
        "home" -> "home"
        "quran" -> "quran"
        "sebha" -> "sebha"
        "dua" -> "dua"
        "leaderboard" -> "podium"
        "quiz" -> "quiz"
        else -> ""
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        ClayImage(
            resName = assetName,
            fallbackIcon = item.icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(if (isSelected) 24.dp else 22.dp)
        )
    }
}
