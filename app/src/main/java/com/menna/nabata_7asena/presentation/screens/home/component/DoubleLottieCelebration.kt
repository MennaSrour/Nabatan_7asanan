package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun DoubleLottieCelebration(spec1: LottieCompositionSpec, spec2: LottieCompositionSpec) {
    val c1 by rememberLottieComposition(spec1)
    val c2 by rememberLottieComposition(spec2)
    Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
        LottieAnimation(
            c1,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize()
        )
        LottieAnimation(
            c2,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(110.dp)
        )
    }
}