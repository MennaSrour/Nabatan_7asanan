package com.menna.nabatan_7asena.presentation.screens.start.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.delay

@Composable
fun NabatanStartContent() {

    var trunkGrown by remember { mutableStateOf(false) }
    var leaf1 by remember { mutableStateOf(false) }
    var leaf2 by remember { mutableStateOf(false) }
    var leaf3 by remember { mutableStateOf(false) }
    var timurVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        trunkGrown = true
        delay(500)
        leaf1 = true
        delay(140)
        leaf2 = true
        delay(140)
        leaf3 = true
        delay(350)
        timurVisible = true
        delay(400)
        titleVisible = true
        delay(200)
        subtitleVisible = true
    }

    val trunkGrowth by animateFloatAsState(
        targetValue = if (trunkGrown) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "trunkGrowth"
    )
    val leaf1Scale by animateFloatAsState(
        targetValue = if (leaf1) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium),
        label = "leaf1"
    )
    val leaf2Scale by animateFloatAsState(
        targetValue = if (leaf2) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium),
        label = "leaf2"
    )
    val leaf3Scale by animateFloatAsState(
        targetValue = if (leaf3) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium),
        label = "leaf3"
    )
    val timurScale by animateFloatAsState(
        targetValue = if (timurVisible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "timurScale"
    )
    val timurRotation by animateFloatAsState(
        targetValue = if (timurVisible) 0f else -14f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessVeryLow),
        label = "timurRotation"
    )
    val titleScale by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0.5f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "titleScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(350),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(450),
        label = "subtitleAlpha"
    )
    val subtitleOffsetY by animateFloatAsState(
        targetValue = if (subtitleVisible) 0f else 18f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium),
        label = "subtitleOffsetY"
    )

    val idleFloat = rememberIdleFloat()
    val idleSway = rememberIdleSway()
    val bgShift = rememberBreathingShift()

    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
            Brush.verticalGradient(
                colors = listOf(
                    lerp(SummerTheme.colors.wisdomStart, SummerTheme.colors.backgroundLight, bgShift),
                    SummerTheme.colors.dayBackground
                )
            )
        )
            .padding(top = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        SplashAtmosphere(isDark = isDark)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            GrowingTreeWithTimur(
                trunkGrowth = trunkGrowth,
                leaf1Scale = leaf1Scale,
                leaf2Scale = leaf2Scale,
                leaf3Scale = leaf3Scale,
                timurScale = timurScale,
                timurRotation = timurRotation,
                idleFloat = if (timurVisible) idleFloat else 0f,
                leafSway = if (trunkGrown) idleSway else 0f
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "نباتاً حسناً",
                fontFamily = NabatanFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = SummerTheme.colors.wisdomAccent,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .graphicsLayer {
                    scaleX = titleScale
                    scaleY = titleScale
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = genderedStringResource(GenderedStrings.startWelcome),
                fontFamily = NabatanFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = SummerTheme.colors.textBrown,
                modifier = Modifier
                    .alpha(subtitleAlpha)
                    .graphicsLayer { translationY = subtitleOffsetY }
            )

            Spacer(Modifier.weight(1f))

            BouncingDotsLoader()

            Spacer(Modifier.height(40.dp))
        }
    }
}
