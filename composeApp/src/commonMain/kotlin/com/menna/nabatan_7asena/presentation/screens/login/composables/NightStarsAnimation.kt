package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.LottieConstants
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition

@Composable
fun NightStarsAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("night_stars.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
