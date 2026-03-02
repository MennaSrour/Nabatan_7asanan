package com.menna.nabata_7asena.presentation.screens.start

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.menna.nabata_7asena.R
import kotlinx.coroutines.delay

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
        var player1: MediaPlayer? = null
        var player2: MediaPlayer? = null

        try {
            player1 = MediaPlayer.create(context, R.raw.kol_sana)
            player2 = MediaPlayer.create(context, R.raw.ramadan_kareem)

            player1?.start()

            player1?.setOnCompletionListener { mp1 ->
                try {
                    mp1.release()

                    player2?.start()

                    player2?.setOnCompletionListener { mp2 ->
                        try {
                            mp2.release()

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
            }
        } catch (e: Exception) {
            e.printStackTrace()
            audioFinished = true
        }

        onDispose {
            try {
                player1?.let {
                    if (it.isPlaying) it.stop()
                    it.release()
                }
                player2?.let {
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
        RamadanStartContent(
        )
    }
}

@Composable
fun RamadanStartContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF3949AB),
                        Color(0xFF5C6BC0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HangingLightsAnimation()

            Spacer(Modifier.height(40.dp))

            TimurImage()

            Spacer(Modifier.height(32.dp))

            AppTitle()

            Spacer(Modifier.height(16.dp))

            RamadanSubtitle()

            Spacer(Modifier.weight(1f))

            LoadingIndicator()

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun HangingLightsAnimation() {
    val lightsComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lights)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LottieAnimation(
            composition = lightsComposition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun TimurImage() {
    val infiniteTransition = rememberInfiniteTransition(label = "timur")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.4f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .graphicsLayer {
                    scaleX = scale * 1.1f
                    scaleY = scale * 1.1f
                    alpha = 0.3f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Image(
            painter = painterResource(id = R.drawable.timur_ss),
            contentDescription = "Timur",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(180.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationY = offsetY
                }
        )
    }
}

@Composable
private fun AppTitle() {
    var visible by remember { mutableStateOf(false) }
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1200),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer { alpha = textAlpha }
    ) {

        Spacer(Modifier.height(12.dp))

        Text(
            text = "\uD83C\uDF31نباتاً حسناً",
            color = Color(0xFFFFD54F),
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun RamadanSubtitle() {
    var visible by remember { mutableStateOf(false) }
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1200),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer { alpha = textAlpha }
    ) {
        Text(
            text = "🌙رمضان كريم",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFFD54F),
            modifier = Modifier.size(32.dp),
            strokeWidth = 4.dp
        )
    }
}