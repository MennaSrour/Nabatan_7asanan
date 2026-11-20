package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 20.dp else 6.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "indicator_width"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(6.dp)
            .clip(CircleShape)
            .background(
            if (isSelected) SummerTheme.colors.white
            else SummerTheme.colors.white.copy(alpha = 0.45f)
        )
    )
}
