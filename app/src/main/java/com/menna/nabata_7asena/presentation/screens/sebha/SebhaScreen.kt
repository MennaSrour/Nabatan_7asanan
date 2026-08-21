package com.menna.nabata_7asena.presentation.screens.sebha

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.presentation.components.CelebrationOverlaySebha
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun SebhaScreen(viewModel: SebhaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    AnimatedContent(targetState = state.isSelectionMode, label = "azkar_transition") { isSelection ->
        if (isSelection) {
            ZikrSelectionMenuSummer(azkarList = viewModel.azkarList, onSelect = { viewModel.selectZikr(it) })
        } else {
            ZikrCounterViewSummer(
                state = state,
                onBack = { viewModel.backToMenu() },
                onTasbeeh = { viewModel.onTasbeehClick() },
                onReset = { viewModel.resetCounter() },
                onToggleSound = { viewModel.toggleAudio() },
                onDismissCelebration = { viewModel.dismissCelebration() }
            )
        }
    }
}

@Composable
fun ZikrSelectionMenuSummer(azkarList: List<ZikrItem>, onSelect: (ZikrItem) -> Unit) {
    val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
    Box(modifier = Modifier.fillMaxSize()) {
        SebhaSummerBackground(particleAlphas)
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
            Card(
                shape = SummerTheme.Shapes.ExtraRounded,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7).copy(0.9f)),
                border = BorderStroke(2.dp, SummerTheme.Colors.PrimaryGold.copy(0.5f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📿", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("اختر ذكرك الآن", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                    Spacer(Modifier.height(4.dp))
                    Text("واكسب نجوم الأذكار البراقة ⭐", fontSize = 13.sp, color = Color(0xFFE65100).copy(0.9f))
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(azkarList) { zikr -> ZikrCardSummer(zikr, onSelect) }
            }
        }
    }
}

@Composable
fun ZikrCardSummer(zikr: ZikrItem, onClick: (ZikrItem) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    Box(
        modifier = Modifier
            .height(160.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(SummerTheme.Shapes.ExtraRounded)
            .background(
                Brush.verticalGradient(
                    listOf(Color(zikr.color).copy(0.12f), Color.White.copy(0.95f))
                )
            )
            .border(1.5.dp, Color(zikr.color).copy(0.4f), SummerTheme.Shapes.ExtraRounded)
            .clickable { isPressed = true; onClick(zikr) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(Color(zikr.color).copy(0.2f)),
                contentAlignment = Alignment.Center
            ) { Text("📿", fontSize = 26.sp) }
            Spacer(Modifier.height(10.dp))
            Text(zikr.text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), textAlign = TextAlign.Center, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Surface(color = Color(zikr.color).copy(0.85f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${zikr.target}", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text("☀️", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ZikrCounterViewSummer(
    state: AzkarState,
    onBack: () -> Unit,
    onTasbeeh: () -> Unit,
    onReset: () -> Unit,
    onToggleSound: () -> Unit,
    onDismissCelebration: () -> Unit
) {
    val zikr = state.currentZikr ?: return
    val view = LocalView.current
    val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SebhaSummerBackground(particleAlphas)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(zikr.color).copy(0.06f), Color(zikr.color).copy(0.12f))
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color(0xFF37474F))
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("السبحة الصيفية 📿", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                    Text("اذكر ربك يا بطل ☀️", fontSize = 12.sp, color = Color(0xFF37474F).copy(0.8f))
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(SummerTheme.Shapes.ExtraRounded)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(zikr.color).copy(0.15f), Color.White, Color(zikr.color).copy(0.15f))
                            )
                        )
                        .border(1.5.dp, Color(zikr.color).copy(0.35f), SummerTheme.Shapes.ExtraRounded)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(zikr.text, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF37474F), textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(36.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(280.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onTasbeeh()
                        }
                ) {
                    val progress = state.currentCount.toFloat() / zikr.target.toFloat()
                    val animatedProgress by animateFloatAsState(progress, label = "progress")

                    Canvas(modifier = Modifier.fillMaxSize().rotate(rotation)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(zikr.color).copy(0.2f), Color(zikr.color).copy(0.04f), Color.Transparent)
                            ),
                            radius = size.width * 0.52f
                        )
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color.White.copy(0.4f), style = Stroke(width = 22.dp.toPx()))
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(Color(zikr.color).copy(0.6f), Color(zikr.color), Color(zikr.color).copy(0.8f))
                            ),
                            startAngle = -90f, sweepAngle = 360 * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📿", fontSize = 28.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("${state.currentCount}", fontSize = 72.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F))
                        Text("/ ${zikr.target}", fontSize = 20.sp, color = Color(zikr.color).copy(0.9f), fontWeight = FontWeight.Bold)
                    }

                    if (progress >= 1f) CelebrationOverlaySebha(onDismiss = onDismissCelebration)
                }

                Spacer(Modifier.height(36.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                    SummerActionButton(icon = Icons.Rounded.Refresh, color = Color(0xFF37474F).copy(0.6f), onClick = onReset)
                    SummerActionButton(
                        icon = if (state.isPlayingAudio) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                        color = if (state.isPlayingAudio) Color(zikr.color) else Color(0xFF37474F).copy(0.6f),
                        onClick = onToggleSound
                    )
                }

                Spacer(Modifier.weight(1f))

                Text("اضغط في أي مكان للعد ☀️", color = Color(0xFF37474F).copy(0.5f), fontSize = 13.sp, modifier = Modifier.padding(bottom = 100.dp))
            }
        }
    }
}

@Composable
fun SummerActionButton(icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = CircleShape,
        color = color.copy(0.12f), modifier = Modifier.size(60.dp),
        border = BorderStroke(1.5.dp, color.copy(0.35f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun SebhaSummerBackground(particleAlphas: List<Float>) {
    val positions = remember {
        listOf(
            Offset(0.10f, 0.06f) to 3f, Offset(0.80f, 0.04f) to 4f,
            Offset(0.45f, 0.02f) to 3f, Offset(0.65f, 0.10f) to 5f,
            Offset(0.20f, 0.09f) to 3f, Offset(0.90f, 0.13f) to 4f,
        )
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF29B6F6),
                        Color(0xFF81D4FA),
                        Color(0xFFFFFDE7)
                    )
                )
            )
            positions.forEachIndexed { i, (off, r) ->
                drawCircle(
                    color = Color.White.copy(0.7f), radius = r.dp.toPx(),
                    center = Offset(size.width * off.x, size.height * off.y),
                    alpha = particleAlphas.getOrElse(i) { 0.7f }
                )
            }
        }

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .alpha(0.5f)
        )
    }
}