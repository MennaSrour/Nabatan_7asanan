package com.menna.nabatan_7asena.presentation.screens.quiz

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
private val GoldMedal = Color(0xFFFFC83D)
private val SilverMedal = Color(0xFFB0BEC5)
private val BronzeMedal = Color(0xFFD7A86E)

@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when {
             state.preCompetitionCountdown != null ->
                PreCompetitionCountdownScreen(value = state.preCompetitionCountdown!!)

            state.mode == QuizMode.NONE -> QuizOverviewContent(
                state = state,
                onStudyClick = viewModel::startStudyMode,
                onCompetitionClick = viewModel::startCompetitionMode
            )

            else -> QuizFlashCardView(
                state = state,
                onSelectAnswer = viewModel::selectAnswer,
                onNextCard = viewModel::goToNextCard,
                onExit = viewModel::exitQuiz
            )
        }
    }
}



@Composable
private fun PreCompetitionCountdownScreen(value: Int) {
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



@Composable
private fun QuizOverviewContent(
    state: QuizUiState,
    onStudyClick: () -> Unit,
    onCompetitionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SummerTheme.colors.wisdomStart, SummerTheme.colors.dayBackground)
                )
            )
    ) {
        LivelyBackgroundDecor(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            MascotHeader(isCompetitionDay = state.isCompetitionDay)
            Spacer(Modifier.height(22.dp))

            if (state.isLoading) {
                LoadingSprout()
            } else {
                state.errorMessage?.let { errorMessage ->
                    ErrorNote(genderedStringResource(errorMessage))
                    Spacer(Modifier.height(16.dp))
                }

                Box(modifier = Modifier.popIn(delayMillis = 0)) {
                    if (state.winners.isNotEmpty()) {
                        WinnersSection(state.winners)
                    } else {
                        EmptyWinnersHint()
                    }
                }

                Spacer(Modifier.height(28.dp))
                if (state.isCompetitionDay) {
                    Box(modifier = Modifier.popIn(delayMillis = 150)) {
                        CompetitionEntryCard(isCompetitionDay = true, onClick = onCompetitionClick)
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.popIn(delayMillis = 300)) {
                        StudyEntryCard(onClick = onStudyClick)
                    }
                } else {
                    Box(modifier = Modifier.popIn(delayMillis = 150)) {
                        StudyEntryCard(onClick = onStudyClick)
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.popIn(delayMillis = 300)) {
                        CompetitionEntryCard(isCompetitionDay = false, onClick = onCompetitionClick)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MascotHeader(isCompetitionDay: Boolean) {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 5f, durationMillis = 900)
    val bubbleShape = RoundedCornerShape(topStart = 6.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 24.dp)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = bounce.dp)
                .clip(CircleShape)
                .background(SummerTheme.colors.goldWarm.copy(alpha = 0.35f))
                .border(4.dp, SummerTheme.colors.cardBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🌻", fontSize = 44.sp)
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(bubbleShape)
                .background(SummerTheme.colors.cardBackground)
                .border(2.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.6f), bubbleShape)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            KidsText(genderedStringResource(GenderedStrings.quizFridayChallenge), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            KidsText(
                if (isCompetitionDay) genderedStringResource(GenderedStrings.quizCompetitionDay)
                else genderedStringResource(GenderedStrings.quizStudyToday),
                fontSize = 13.sp,
                color = SummerTheme.colors.textBrown,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoadingSprout() {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 10f, durationMillis = 700)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌱", fontSize = 60.sp, modifier = Modifier.offset(y = bounce.dp))
            Spacer(Modifier.height(8.dp))
            KidsText(genderedStringResource(GenderedStrings.quizLoading), fontSize = 15.sp, color = SummerTheme.colors.textBrown)
        }
    }
}

@Composable
private fun ErrorNote(message: String) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Candy.Coral.copy(alpha = 0.15f))
            .border(2.dp, Candy.Coral.copy(alpha = 0.6f), shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🙈", fontSize = 24.sp)
        Spacer(Modifier.width(10.dp))
        KidsText(message, fontSize = 13.sp, color = SummerTheme.colors.textDarkBrown)
    }
}



@Composable
private fun WinnersSection(winners: List<QuizWinner>) {
    val panelShape = RoundedCornerShape(30.dp)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StickerLabel(genderedStringResource(GenderedStrings.quizLastWinners), color = Candy.Orange, rotation = -2f)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(panelShape)
                .background(SummerTheme.colors.cardBackground.copy(alpha = 0.55f))
                .border(2.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.35f), panelShape)
                .padding(top = 34.dp, start = 8.dp, end = 8.dp)
        ) {
            WinnersPodium(winners)
        }
    }
}

@Composable
private fun WinnersPodium(winners: List<QuizWinner>) {
    val first = winners.getOrNull(0)
    val second = winners.getOrNull(1)
    val third = winners.getOrNull(2)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        second?.let { PodiumSpot(it, rank = 2, medalColor = SilverMedal, medalEmoji = "🥈", height = 92.dp) }
        first?.let { PodiumSpot(it, rank = 1, medalColor = GoldMedal, medalEmoji = "🥇", height = 120.dp) }
        third?.let { PodiumSpot(it, rank = 3, medalColor = BronzeMedal, medalEmoji = "🥉", height = 76.dp) }
    }
}

@Composable
private fun PodiumSpot(
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

@Composable
private fun EmptyWinnersHint() {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 5f, durationMillis = 1000)
    val dashColor = SummerTheme.colors.goldDeep.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(26.dp.toPx()),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f))
                    )
                )
            }
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏆", fontSize = 46.sp, modifier = Modifier.offset(y = bounce.dp))
            Spacer(Modifier.height(8.dp))
            KidsText(
                genderedStringResource(GenderedStrings.quizFirstWinner),
                fontSize = 14.sp,
                color = SummerTheme.colors.textBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
private fun EntryCardContent(
    emoji: String,
    title: String,
    subtitle: String,
    titleColor: Color,
    subtitleColor: Color,
    emojiModifier: Modifier = Modifier,
    useShadow: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 32.sp, modifier = emojiModifier)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            KidsText(
                title,
                fontSize = 18.sp,
                color = titleColor,
                fontWeight = FontWeight.ExtraBold,
                shadow = if (useShadow) ButtonTextShadow else null
            )
            Spacer(Modifier.height(2.dp))
            KidsText(
                subtitle,
                fontSize = 12.sp,
                color = subtitleColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StudyEntryCard(onClick: () -> Unit) {
    ChunkyButton(
        onClick = onClick,
        color = Candy.Leaf,
        edgeColor = Candy.LeafEdge,
        onClickLabel = genderedStringResource(GenderedStrings.quizStudyAction),
        modifier = Modifier
            .fillMaxWidth()
            .rotate(-1.2f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
    ) {
        EntryCardContent(
            emoji = "📖",
            title = genderedStringResource(GenderedStrings.quizStudyAction),
            subtitle = genderedStringResource(GenderedStrings.quizStudyHint),
            titleColor = Color.White,
            subtitleColor = Color.White.copy(alpha = 0.92f)
        )
    }
}


@Composable
private fun CompetitionEntryCard(isCompetitionDay: Boolean, onClick: () -> Unit) {
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

@Composable
private fun rememberCompetitionPulse(): Float {
    val infinite = rememberInfiniteTransition(label = "competition_pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return scale
}