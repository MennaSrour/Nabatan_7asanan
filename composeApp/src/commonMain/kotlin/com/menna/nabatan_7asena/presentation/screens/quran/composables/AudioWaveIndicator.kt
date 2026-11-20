package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun AudioWaveIndicator() {
    val q = SummerTheme.quran
    val transition = rememberInfiniteTransition(label = "wave")
    val bars = List(5) { i ->
        transition.animateFloat(
            initialValue  = 4f,
            targetValue   = 16f,
            animationSpec = infiniteRepeatable(
                animation  = tween(380 + i * 70, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$i"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        modifier              = Modifier.height(20.dp)
    ) {
        bars.forEach { bar ->
            val h by bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .background(q.accent.copy(0.8f), RoundedCornerShape(2.dp))
            )
        }
    }
}
