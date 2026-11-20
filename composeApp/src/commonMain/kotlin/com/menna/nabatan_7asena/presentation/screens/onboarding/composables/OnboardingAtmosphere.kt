package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.LottieConstants
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition
import com.menna.nabatan_7asena.resources.AppResources as R

@Composable
fun OnboardingAtmosphere(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(if (isDark) R.raw.night_stars else R.raw.clouds)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = if (isDark) 0.55f else 0.35f
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer { alpha = if (isDark) 0.85f else 0.55f }
    )
}
