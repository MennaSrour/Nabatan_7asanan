package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerGrowingTree(totalStars: Int) {
    val colors = SummerTheme.colors
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.growing_tree))
    val targetProgress = when {
        totalStars < 50  -> (totalStars / 50f) * 0.33f
        totalStars < 150 -> 0.33f + ((totalStars - 50) / 100f) * 0.34f
        else             -> 0.67f + ((totalStars - 150) / 150f) * 0.33f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(targetProgress, tween(1000, easing = LinearEasing), label = "tree")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(colors.white.copy(alpha = 0.2f), CircleShape)
            .padding(8.dp)
    ) {
        LottieAnimation(composition = composition, progress = { animatedProgress }, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.width(70.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = colors.goldWarm,
            trackColor = colors.white.copy(0.3f)
        )
    }
}
