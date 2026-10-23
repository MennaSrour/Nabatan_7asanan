package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
internal fun CompetitionEntryCard(isCompetitionDay: Boolean, onClick: () -> Unit) {
    val pulse = if (isCompetitionDay) rememberCompetitionPulse() else 1f
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val colors = SummerTheme.colors

    val trophyInfinite = rememberInfiniteTransition(label = "trophy_wobble")
    val trophyTilt by trophyInfinite.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "trophy_tilt"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulse)
            .offset { IntOffset(shake.value.roundToInt(), 0) }
            .rotate(if (isCompetitionDay) 1.2f else 0f)
    ) {
        ChunkyButton(
            onClick = {
                if (isCompetitionDay) {
                    onClick()
                } else {
                    scope.launch {
                        for (x in listOf(-14f, 14f, -10f, 10f, -5f, 0f)) {
                            shake.animateTo(x, tween(55))
                        }
                    }
                }
            },
            color = if (isCompetitionDay) Candy.Orange else colors.chestLockedStart,
            edgeColor = if (isCompetitionDay) Candy.OrangeEdge else colors.chestLockedEnd,
            onClickLabel = genderedStringResource(GenderedStrings.quizStartCompetition),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
        ) {
            if (isCompetitionDay) {
                EntryCardContent(
                    emoji = "🏆",
                    title = genderedStringResource(GenderedStrings.quizStartCompetitionTitle),
                    subtitle = genderedStringResource(GenderedStrings.quizCompetitionHint),
                    titleColor = Color.White,
                    subtitleColor = Color.White.copy(alpha = 0.92f),
                    emojiModifier = Modifier.rotate(trophyTilt)
                )
            } else {
                EntryCardContent(
                    emoji = "🔒",
                    title = genderedStringResource(GenderedStrings.quizFridayChallenge),
                    subtitle = genderedStringResource(GenderedStrings.quizCompetitionClosed),
                    titleColor = colors.chestLockedText,
                    subtitleColor = colors.chestLockedText.copy(alpha = 0.85f),
                    useShadow = false
                )
            }
        }

        if (isCompetitionDay) {
            StickerLabel(
                text = genderedStringResource(GenderedStrings.quizToday),
                color = Candy.Coral,
                rotation = 6f,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = (-14).dp)
            )
        }
    }
}
