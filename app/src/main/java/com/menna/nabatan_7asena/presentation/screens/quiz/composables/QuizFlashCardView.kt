package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quiz.AnswerResult
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizCardState
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizMode
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun QuizFlashCardView(
    state: QuizUiState,
    onSelectAnswer: (Int) -> Unit,
    onNextCard: () -> Unit,
    onExit: () -> Unit
) {
    val isCompetition = state.mode == QuizMode.COMPETITION
    val cards = if (isCompetition) state.competitionCards else state.studyCards

    if (state.competitionFinished) {
        CompetitionFinishedContent(score = state.competitionScore, total = cards.size, onDone = onExit)
        return
    }

    val card = cards.getOrNull(state.currentIndex) ?: return
    val scrollState = rememberScrollState()
    LaunchedEffect(state.currentIndex) { scrollState.scrollTo(0) }

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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundCloseButton(onClick = onExit)
                Spacer(Modifier.width(12.dp))
                PlantProgress(
                    answeredCount = state.currentIndex + if (card.isFlipped) 1 else 0,
                    total = cards.size,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                CounterChip(text = "${state.currentIndex + 1}/${cards.size}")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCompetition) {
                    StickerLabel(genderedStringResource(GenderedStrings.quizCompetitionRule), color = Candy.Orange, rotation = -2f)
                } else {
                    StickerLabel(genderedStringResource(GenderedStrings.quizTakeYourTime), color = Candy.Leaf, rotation = -2f)
                }
                Spacer(Modifier.weight(1f))
                StreakBadge(streak = state.streak)
                if (isCompetition) {
                    Spacer(Modifier.width(8.dp))
                    QuestionTimerPill(timeLeftMs = state.questionTimeLeftMs)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        key(state.currentIndex) {
                            Box(
                                modifier = Modifier
                                    .rotate(if (state.currentIndex % 2 == 0) -1.2f else 1.2f)
                                    .popIn()
                            ) {
                                FlipCard(
                                    card = card,
                                    number = state.currentIndex + 1,
                                    onSelectAnswer = onSelectAnswer
                                )
                            }
                        }
                        PointsFlyup(
                            token = state.answerFeedbackToken,
                            points = state.lastAnswerPoints,
                            isCorrect = state.lastAnswerCorrect == true,
                            modifier = Modifier.offset(y = (-16).dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    NextButtonArea(
                        visible = card.isFlipped,
                        isLast = state.currentIndex + 1 >= cards.size,
                        isCompetition = isCompetition,
                        onClick = onNextCard
                    )
                }
            }
        }
    }
}

@Composable
private fun FlipCard(
    card: QuizCardState,
    number: Int,
    onSelectAnswer: (Int) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped) 180f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    val shape = RoundedCornerShape(32.dp)
    val colors = SummerTheme.colors
    val borderColor = when {
        !card.isFlipped -> colors.wisdomAccent
        card.result == AnswerResult.CORRECT -> Candy.Leaf
        else -> Candy.Coral
    }
    val edgeColor = when {
        !card.isFlipped -> colors.wisdomAccent.copy(alpha = 0.4f)
        card.result == AnswerResult.CORRECT -> Candy.LeafEdge
        else -> Candy.CoralEdge
    }
    val tint = when {
        !card.isFlipped -> Color.Transparent
        card.result == AnswerResult.CORRECT -> Candy.Leaf.copy(alpha = 0.14f)
        else -> Candy.Coral.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .height(410.dp)
            .graphicsLayer {
            rotationY = rotation
            cameraDistance = 14 * density
        }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 10.dp)
                .clip(shape)
                .background(edgeColor)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(colors.cardBackground)
                .background(tint)
                .border(3.dp, borderColor, shape)
        ) {
            if (rotation <= 90f) {
                QuestionFace(card = card, number = number, onSelectAnswer = onSelectAnswer)
            } else {
                Box(modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }) {
                    AnswerFace(card = card)
                }
            }
        }
    }
}

private val answerLetters = listOf("أ", "ب", "ج", "د", "هـ")

@Composable
private fun QuestionFace(card: QuizCardState, number: Int, onSelectAnswer: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StickerLabel(genderedStringResource(GenderedStrings.quizQuestionNumber, number), color = SummerTheme.colors.wisdomAccent, rotation = -3f)

        Spacer(Modifier.height(12.dp))

        KidsText(
            card.question.text,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 29.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        card.question.choices.forEachIndexed { index, choice ->
            val (color, edge) = Candy.choicePairs[index % Candy.choicePairs.size]
            ChunkyButton(
                onClick = { onSelectAnswer(index) },
                color = color,
                edgeColor = edge,
                onClickLabel = choice,
                shape = RoundedCornerShape(20.dp),
                depth = 5.dp,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 11.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        KidsText(
                            answerLetters.getOrElse(index) { "${index + 1}" },
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    KidsText(
                        choice,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        shadow = ButtonTextShadow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private val correctPraises = listOf(GenderedStrings.quizPraiseHero, GenderedStrings.quizPraiseClever, GenderedStrings.quizPraiseContinue, GenderedStrings.quizPraiseGenius)
private val wrongEncouragements = listOf(GenderedStrings.quizAlmost, GenderedStrings.quizTryAgain, GenderedStrings.quizLearning)

@Composable
private fun AnswerFace(card: QuizCardState) {
    val isCorrect = card.result == AnswerResult.CORRECT
    val timedOut = card.selectedIndex == null
    val headline = remember(card.question.text, card.result) {
        val seed = card.question.text.length
        when {
            isCorrect -> correctPraises[seed % correctPraises.size]
            timedOut -> GenderedStrings.quizTimeOut
            else -> wrongEncouragements[seed % wrongEncouragements.size]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isCorrect) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
            LottieAnimation(composition, iterations = 1, modifier = Modifier.size(110.dp))
        } else {
            WrongAnswerAnimation(emoji = if (timedOut) "⏰" else "😅")
        }

        Spacer(Modifier.height(10.dp))

        KidsText(
            genderedStringResource(headline),
            fontSize = 22.sp,
            color = if (isCorrect) Candy.Leaf else Candy.Coral,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))

        KidsText(
            genderedStringResource(GenderedStrings.quizCorrectAnswer),
            fontSize = 13.sp,
            color = SummerTheme.colors.textBrown,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))

        ChunkyButton(
            onClick = {},
            enabled = false,
            color = Candy.Leaf,
            edgeColor = Candy.LeafEdge,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
        ) {
            KidsText(
                card.question.choices[card.question.correctIndex],
                fontSize = 19.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                shadow = ButtonTextShadow
            )
        }

        if (!isCorrect && !timedOut) {
            Spacer(Modifier.height(10.dp))
            KidsText(
                genderedStringResource(GenderedStrings.quizSelectedAnswer, card.question.choices.getOrNull(card.selectedIndex ?: -1).orEmpty()),
                fontSize = 12.sp,
                color = SummerTheme.colors.textBrown.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WrongAnswerAnimation(emoji: String) {
    val infinite = rememberInfiniteTransition(label = "wrong_bounce")
    val offsetY by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wrong_offset"
    )
    val tilt by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(650, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wrong_tilt"
    )
    Box(
        modifier = Modifier
            .size(84.dp)
            .offset(y = offsetY.dp)
            .rotate(tilt)
            .clip(CircleShape)
            .background(Candy.Coral.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 46.sp)
    }
}

@Composable
private fun RoundCloseButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .bouncyClickable(onClickLabel = genderedStringResource(GenderedStrings.exit), onClick = onClick)
            .clip(CircleShape)
            .background(SummerTheme.colors.cardBackground)
            .border(2.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Rounded.Close,
            contentDescription = genderedStringResource(GenderedStrings.exit),
            tint = SummerTheme.colors.textDarkBrown,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun CounterChip(text: String) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(SummerTheme.colors.cardBackground)
            .border(2.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.55f), shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        KidsText(text, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun PlantProgress(answeredCount: Int, total: Int, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = if (total == 0) 0f else (answeredCount.toFloat() / total).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "plant_progress"
    )
    val f = fraction.coerceIn(0f, 1f)
    val emoji = when {
        f >= 0.99f -> "🌻"
        f >= 0.66f -> "🌷"
        f >= 0.33f -> "🌿"
        else -> "🌱"
    }

    BoxWithConstraints(modifier = modifier.height(30.dp)) {
        val emojiOffset = (maxWidth - 26.dp) * f

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .background(SummerTheme.colors.cardBackground)
                .border(2.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.45f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(f)
                .height(14.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Candy.Leaf, Candy.Gold)))
        )
        Text(
            emoji,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = emojiOffset)
        )
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    if (streak < 2) return

    val scale = remember { Animatable(1f) }
    LaunchedEffect(streak) {
        scale.snapTo(1.5f)
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    val shape = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .scale(scale.value)
            .clip(shape)
            .background(Candy.Orange)
            .border(2.dp, Color.White, shape)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text("🔥", fontSize = 15.sp)
        Spacer(Modifier.width(3.dp))
        KidsText("$streak", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun QuestionTimerPill(timeLeftMs: Long, totalMs: Long = QuizUiState.QUESTION_TIME_MS) {
    val target = (timeLeftMs.toFloat() / totalMs).coerceIn(0f, 1f)
    val fraction by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(100),
        label = "timer_fraction"
    )
    val isLow = target <= 0.3f
    val fillColor = if (isLow) Candy.Wrong else Candy.Leaf
    val secondsLeft = ((timeLeftMs + 999L) / 1000L).toInt().coerceAtLeast(0)

    val infinite = rememberInfiniteTransition(label = "timer_wiggle")
    val wiggle by infinite.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(120), RepeatMode.Reverse),
        label = "wiggle"
    )

    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(38.dp)
            .clip(shape)
            .background(SummerTheme.colors.cardBackground)
            .border(2.5.dp, fillColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(fillColor.copy(alpha = 0.3f))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⏰", fontSize = 16.sp, modifier = Modifier.rotate(if (isLow) wiggle else 0f))
            Spacer(Modifier.width(4.dp))
            KidsText(
                "$secondsLeft",
                fontSize = 18.sp,
                color = if (isLow) Candy.Wrong else SummerTheme.colors.textDarkBrown,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun PointsFlyup(token: Int, points: Int, isCorrect: Boolean, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (isCorrect && points > 0) {
            visible = true
            delay(950)
            visible = false
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (visible) {
            ConfettiBurst()
        }
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 300f)
            ) + fadeIn(),
            exit = fadeOut(tween(400)) + slideOutVertically(
                targetOffsetY = { -it * 2 },
                animationSpec = tween(600)
            )
        ) {
            KidsText(
                "+$points 🎉",
                fontSize = 30.sp,
                color = Candy.Leaf,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private val burstEmojis = listOf("✨", "⭐", "💛", "🌟", "🍃", "✨")

@Composable
private fun ConfettiBurst() {
    val particleAngles = remember { List(burstEmojis.size) { i -> (i * 60f) + Random.nextInt(-15, 15) } }

    particleAngles.forEachIndexed { i, angle ->
        val progress = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            progress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        }
        val radius = 48f * progress.value
        val radians = Math.toRadians(angle.toDouble())
        val dx = (radius * cos(radians)).toFloat()
        val dy = (radius * sin(radians)).toFloat()
        Text(
            burstEmojis[i],
            fontSize = 15.sp,
            modifier = Modifier
                .offset(x = dx.dp, y = dy.dp)
                .alpha((1f - progress.value).coerceIn(0f, 1f))
        )
    }
}

@Composable
private fun CompetitionFinishedContent(score: Int, total: Int, onDone: () -> Unit) {
    val percentage = if (total == 0) 0f else score.toFloat() / total
    val starsEarned = when {
        percentage >= 0.8f -> 3
        percentage >= 0.5f -> 2
        score > 0 -> 1
        else -> 0
    }
    val resultMessage = when {
        percentage >= 0.8f -> genderedStringResource(GenderedStrings.quizResultHero)
        percentage >= 0.5f -> genderedStringResource(GenderedStrings.quizResultGood)
        else -> genderedStringResource(GenderedStrings.quizResultNextTime)
    }

    var displayedScore by remember { mutableStateOf(0) }
    LaunchedEffect(score) {
        for (value in 0..score) {
            displayedScore = value
            delay(120)
        }
    }

    val rotation = SummerTheme.rememberSlowRotation(durationMillis = 20000)
    val trophyBounce = SummerTheme.rememberFloatingAnimation(range = 8f, durationMillis = 900)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Candy.Orange, Candy.Gold))),
        contentAlignment = Alignment.Center
    ) {
        SunburstRays(
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = 0.18f),
            rotation = rotation
        )
        if (starsEarned >= 1) {
            ConfettiRain(modifier = Modifier.fillMaxSize())
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("🏆", fontSize = 96.sp, modifier = Modifier.offset(y = trophyBounce.dp))

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sizes = listOf(46, 62, 46)
                repeat(3) { i ->
                    StarPop(index = i, earned = i < starsEarned, sizeSp = sizes[i])
                }
            }

            Spacer(Modifier.height(16.dp))

            KidsText(
                resultMessage,
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                shadow = ButtonTextShadow
            )

            Spacer(Modifier.height(16.dp))

            val panelShape = RoundedCornerShape(28.dp)
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .clip(panelShape)
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                KidsText(genderedStringResource(GenderedStrings.quizScorePrefix), fontSize = 18.sp, color = Candy.InkBrown, modifier = Modifier.padding(bottom = 8.dp))
                Spacer(Modifier.width(8.dp))
                KidsText(
                    "$displayedScore",
                    fontSize = 52.sp,
                    color = Candy.Orange,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(8.dp))
                KidsText(genderedStringResource(GenderedStrings.quizTotalQuestions, total), fontSize = 18.sp, color = Candy.InkBrown, modifier = Modifier.padding(bottom = 8.dp))
            }

            Spacer(Modifier.height(32.dp))

            ChunkyButton(
                onClick = onDone,
                color = Color.White,
                edgeColor = Color(0xFFE2CFA0),
                modifier = Modifier.fillMaxWidth(0.7f),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp)
            ) {
                KidsText(genderedStringResource(GenderedStrings.back), fontSize = 18.sp, color = Candy.OrangeEdge, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun StarPop(index: Int, earned: Boolean, sizeSp: Int) {
    val pop = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(450L + index * 350L)
        pop.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }
    Text(
        "⭐",
        fontSize = sizeSp.sp,
        modifier = Modifier
            .graphicsLayer {
            scaleX = pop.value
            scaleY = pop.value
            rotationZ = (1f - pop.value) * -40f
            alpha = if (earned) 1f else 0.3f
        }
    )
}

@Composable
private fun NextButtonArea(
    visible: Boolean,
    isLast: Boolean,
    isCompetition: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() +
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) +
            slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut()
        ) {
            ChunkyButton(
                onClick = onClick,
                color = Candy.Gold,
                edgeColor = Candy.GoldEdge,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp)
            ) {
                KidsText(
                    text = when {
                        !isLast -> genderedStringResource(GenderedStrings.quizNextQuestion)
                        isCompetition -> genderedStringResource(GenderedStrings.quizViewResult)
                        else -> genderedStringResource(GenderedStrings.done)
                    },
                    fontSize = 18.sp,
                    color = Candy.InkBrown,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
