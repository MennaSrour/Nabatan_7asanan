package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun DoubleLottieCelebration(
    firstAnimation: LottieCompositionSpec.RawRes,
    secondAnimation: LottieCompositionSpec.RawRes
) {
    val confettiComposition by rememberLottieComposition(firstAnimation)
    val trophyComposition by rememberLottieComposition(secondAnimation)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = confettiComposition,
            iterations = 1,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            speed = 1.5f
        )

        LottieAnimation(
            composition = trophyComposition,
            iterations = 1,
            modifier = Modifier.size(300.dp)
        )
    }
}