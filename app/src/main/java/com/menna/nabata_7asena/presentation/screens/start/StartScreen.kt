package com.menna.nabata_7asena.presentation.screens.start

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
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
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import kotlinx.coroutines.delay
private val ColorSkyLight = Color(0xFFE3F2F4)
private val ColorGreenLight = Color(0xFFEAF3E7)
private val ColorForestDeep = Color(0xFF1F4B3F)
private val ColorForestMid = Color(0xFF4C7A63)
private val ColorHaloSoft = Color(0xFF9BC7AE)
private val ColorTrunk = Color(0xFF8C6A4E)
private val ColorLeafDeep = Color(0xFF3F7A5C)
private val ColorLeafMid = Color(0xFF5C9575)
private val ColorLeafLight = Color(0xFF7FAE8E)
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
    var textVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        trunkGrown = true
        delay(550)
        leaf1 = true
        delay(130)
        leaf2 = true
        delay(130)
        leaf3 = true
        delay(450)
        timurVisible = true
        delay(500)
        textVisible = true
    }

    val trunkGrowth by animateFloatAsState(
        targetValue = if (trunkGrown) 1f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "trunkGrowth"
    )
    val leaf1Scale by animateFloatAsState(
        targetValue = if (leaf1) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "leaf1"
    )
    val leaf2Scale by animateFloatAsState(
        targetValue = if (leaf2) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "leaf2"
    )
    val leaf3Scale by animateFloatAsState(
        targetValue = if (leaf3) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "leaf3"
    )
    val timurScale by animateFloatAsState(
        targetValue = if (timurVisible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "timurScale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "textAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(ColorSkyLight, ColorGreenLight))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SkyCloudsAnimation()

            Spacer(Modifier.height(8.dp))

            GrowingTreeWithTimur(
                trunkGrowth = trunkGrowth,
                leaf1Scale = leaf1Scale,
                leaf2Scale = leaf2Scale,
                leaf3Scale = leaf3Scale,
                timurScale = timurScale
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "نباتاً حسناً",
                fontFamily = NabatanFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = ColorForestDeep,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "أهلا يا غرس الأمة",
                fontFamily = NabatanFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = ColorForestMid,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(Modifier.weight(1f))

            LoadingIndicator()

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SkyCloudsAnimation() {
    val cloudsComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.clouds)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .alpha(0.7f),
        contentAlignment = Alignment.TopCenter
    ) {
        LottieAnimation(
            composition = cloudsComposition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun GrowingTreeWithTimur(
    trunkGrowth: Float,
    leaf1Scale: Float,
    leaf2Scale: Float,
    leaf3Scale: Float,
    timurScale: Float
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
                .scale(leaf1Scale)
                .background(ColorLeafDeep, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 6.dp, y = (-72).dp)
                .scale(leaf2Scale)
                .background(ColorLeafMid, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 62.dp, y = (-72).dp)
                .scale(leaf3Scale)
                .background(ColorLeafLight, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-6).dp)
                .scale(timurScale),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(ColorHaloSoft.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.timur_ss),
                contentDescription = "Timur",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(170.dp)
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = ColorForestMid,
            modifier = Modifier.size(30.dp),
            strokeWidth = 3.dp
        )
    }
}