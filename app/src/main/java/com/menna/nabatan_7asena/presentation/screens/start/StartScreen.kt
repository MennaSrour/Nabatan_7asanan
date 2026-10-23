package com.menna.nabatan_7asena.presentation.screens.start

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.delay

private val ColorTrunk = Color(0xFF8C6A4E)

private val NabatanFontFamily = FontFamily(
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_medium, FontWeight.Medium),
    Font(R.font.cairo_semibold, FontWeight.SemiBold),
    Font(R.font.cairo_bold, FontWeight.Bold)
)


@Composable
fun StartScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: StartViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }

    var audioFinished by remember { mutableStateOf(false) }
    var navigationReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var player: MediaPlayer? = null

        try {
            player = MediaPlayer.create(context, R.raw.welcome_kids)
            player?.start()

            player?.setOnCompletionListener { mp ->
                try {
                    mp.release()
                    audioFinished = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    audioFinished = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            audioFinished = true
        }

        onDispose {
            try {
                player?.let {
                    if (it.isPlaying) it.stop()
                    it.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            navigationReady = true

            while (!audioFinished) {
                delay(100)
            }

            isVisible = false
            delay(800)

            when (event) {
                is SplashEvent.NavigateToOnboarding -> onNavigateToOnboarding()
                is SplashEvent.NavigateToLogin -> onNavigateToLogin()
                is SplashEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(500))
    ) {
        NabatanStartContent()
    }
}

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
                text = "أهلا يا غرس الأمة",
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


@Composable
private fun SplashAtmosphere(isDark: Boolean) {
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



@Composable
private fun rememberIdleFloat(): Float {
    val infinite = rememberInfiniteTransition(label = "idleFloat")
    val value by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleFloatValue"
    )
    return value
}

@Composable
private fun rememberIdleSway(): Float {
    val infinite = rememberInfiniteTransition(label = "idleSway")
    val value by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleSwayValue"
    )
    return value
}

@Composable
private fun rememberBreathingShift(): Float {
    val infinite = rememberInfiniteTransition(label = "bgBreathing")
    val value by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgBreathingValue"
    )
    return value
}


@Composable
private fun GrowingTreeWithTimur(
    trunkGrowth: Float,
    leaf1Scale: Float,
    leaf2Scale: Float,
    leaf3Scale: Float,
    timurScale: Float,
    timurRotation: Float,
    idleFloat: Float,
    leafSway: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(78.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 34.dp)
                .graphicsLayer {
                    scaleY = trunkGrowth
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .background(ColorTrunk, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )

        Box(
            modifier = Modifier
                .size(78.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 34.dp, y = (-96).dp)
                .graphicsLayer {
                    scaleX = leaf1Scale
                    scaleY = leaf1Scale
                    rotationZ = leafSway
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .background(SummerTheme.colors.statGreenEnd, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 6.dp, y = (-72).dp)
                .graphicsLayer {
                    scaleX = leaf2Scale
                    scaleY = leaf2Scale
                    rotationZ = -leafSway
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .background(SummerTheme.colors.statGreenStart, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 62.dp, y = (-72).dp)
                .graphicsLayer {
                    scaleX = leaf3Scale
                    scaleY = leaf3Scale
                    rotationZ = leafSway
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .background(
                    lerp(SummerTheme.colors.statGreenStart, SummerTheme.colors.goldWarm, 0.35f),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-6).dp)
                .graphicsLayer {
                    translationY = idleFloat
                    rotationZ = timurRotation
                    scaleX = timurScale
                    scaleY = timurScale
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SummerTheme.colors.goldWarm.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "Timur",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(170.dp)
            )
        }
    }
}


@Composable
private fun BouncingDotsLoader() {
    val infinite = rememberInfiniteTransition(label = "dotsLoader")
    val dotColor = SummerTheme.colors.goldWarm

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) { index ->
            val bounce by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 150, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer {
                        translationY = -bounce * 14f
                        scaleX = 1f - bounce * 0.15f
                        scaleY = 1f + bounce * 0.15f
                    }
                    .background(dotColor, CircleShape)
            )
        }
    }
}