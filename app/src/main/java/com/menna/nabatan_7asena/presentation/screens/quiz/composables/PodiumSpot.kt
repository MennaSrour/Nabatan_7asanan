package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.delay

@Composable
internal fun PodiumSpot(
    winner: QuizWinner,
    rank: Int,
    medalColor: Color,
    medalEmoji: String,
    height: Dp
) {
    val rise = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(
            when (rank) {
                3 -> 100L
                2 -> 300L
                else -> 550L
            }
        )
        rise.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    val infinite = rememberInfiniteTransition(label = "crown")
    val crownTilt by infinite.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "crown_tilt"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(96.dp)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { alpha = rise.value.coerceIn(0f, 1f) }
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                Box(
                    modifier = Modifier
                        .size(if (rank == 1) 70.dp else 58.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, medalColor, CircleShape)
                        .background(SummerTheme.colors.cardBackground)
                ) {
                    Image(
                        painter = painterResource(AvatarCatalog.resFor(winner.gender, winner.avatarIndex)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (rank == 1) {
                    Text(
                        "👑",
                        fontSize = 26.sp,
                        modifier = Modifier
                            .offset(y = (-24).dp)
                            .rotate(crownTilt)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            KidsText(
                winner.name,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            KidsText(
                genderedStringResource(GenderedStrings.quizWinnerScore, winner.score),
                fontSize = 11.sp,
                color = SummerTheme.colors.textBrown.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * rise.value.coerceAtLeast(0f))
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Brush.verticalGradient(listOf(medalColor, medalColor.copy(alpha = 0.7f)))),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(medalEmoji, fontSize = 30.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
