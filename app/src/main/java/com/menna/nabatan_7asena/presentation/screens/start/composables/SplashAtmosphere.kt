package com.menna.nabatan_7asena.presentation.screens.start.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R

@Composable
internal fun SplashAtmosphere(isDark: Boolean) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(if (isDark) R.raw.night_stars else R.raw.clouds)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = if (isDark) 0.55f else 0.35f
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                if (isDark) Modifier.fillMaxHeight()
                else Modifier.height(200.dp).align(Alignment.TopCenter)
            )
                .graphicsLayer { alpha = if (isDark) 0.85f else 0.6f }
        )
    }
}
