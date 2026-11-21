package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import io.github.alexzhirkevich.compottie.rememberLottieAnimatable

@Composable
internal fun TreasureChestIllustration(isUnlocked: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.treasure))
    val animation = rememberLottieAnimatable()
    LaunchedEffect(composition, isUnlocked) {
        val loaded = composition ?: return@LaunchedEffect
        if (isUnlocked) animation.animate(composition = loaded, iterations = 1)
        else animation.snapTo(loaded, progress = 0f)
    }
    Box(Modifier.padding(vertical = 8.dp).size(136.dp).background(
        Brush.radialGradient(listOf(SummerTheme.colors.goldWarm.copy(0.55f), SummerTheme.colors.transparent)), CircleShape),
        contentAlignment = Alignment.Center) {
        LottieAnimation(composition, progress = { animation.value }, modifier = Modifier.size(122.dp))
        TreasureSparkles()
        if (!isUnlocked) Icon(Icons.Rounded.Lock, null, tint = SummerTheme.colors.textBrown,
            modifier = Modifier.align(Alignment.BottomEnd).background(SummerTheme.colors.dialogBackground, CircleShape).padding(8.dp).size(20.dp))
    }
}

@Composable
private fun BoxScope.TreasureSparkles() {
    val alphas = SummerTheme.rememberParticleAlphas(3)
    listOf(Alignment.TopStart, Alignment.CenterEnd, Alignment.BottomStart).forEachIndexed { index, alignment ->
        Icon(Icons.Rounded.AutoAwesome, null, tint = SummerTheme.colors.goldDeep.copy(alpha = alphas[index]),
            modifier = Modifier.align(alignment).size(if (index == 0) 20.dp else 14.dp))
    }
}
