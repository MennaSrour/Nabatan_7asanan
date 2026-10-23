package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@Composable
internal fun PreCompetitionCountdownScreen(value: Int) {
    val rotation = SummerTheme.rememberSlowRotation(durationMillis = 18000)
    val numberColor = when (value) {
        3 -> Candy.Coral
        2 -> Candy.Orange
        else -> Candy.Leaf
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Candy.Violet, Candy.Pink))),
        contentAlignment = Alignment.Center
    ) {
        SunburstRays(
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = 0.13f),
            rotation = rotation
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            KidsText(
                genderedStringResource(GenderedStrings.quizReady),
                fontSize = 34.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                shadow = ButtonTextShadow
            )
            Spacer(Modifier.height(20.dp))

            key(value) {
                val pop = remember { Animatable(0.3f) }
                val tilt = remember { Animatable(-14f) }
                LaunchedEffect(Unit) {
                    launch {
                        pop.animateTo(
                            1f,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                        )
                    }
                    launch {
                        tilt.animateTo(
                            0f,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .graphicsLayer {
                        scaleX = pop.value
                        scaleY = pop.value
                        rotationZ = tilt.value
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .offset(y = 9.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(10.dp, numberColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        KidsText(
                            "$value",
                            fontSize = 110.sp,
                            color = numberColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            KidsText(
                genderedStringResource(GenderedStrings.quizChallenge),
                fontSize = 18.sp,
                color = Color.White,
                shadow = ButtonTextShadow
            )
        }
    }
}
