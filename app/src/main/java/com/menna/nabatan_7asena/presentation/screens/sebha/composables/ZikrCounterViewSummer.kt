package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun ZikrCounterViewSummer(
    state: SebhaUiState,
    onBack: () -> Unit,
    onTasbeeh: () -> Unit,
    onReset: () -> Unit,
    onToggleSound: () -> Unit,
    onDismissCelebration: () -> Unit
) {
    val zikr = state.currentZikr ?: return
    val view = LocalView.current
    val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
    val isUnlimited = zikr.target <= 0

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
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = SummerTheme.colors.textPrimary)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(genderedStringResource(GenderedStrings.sebhaTitle), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SummerTheme.colors.textPrimary)
                    Text("أَلا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ", fontSize = 12.sp, color = SummerTheme.colors.textPrimary.copy(0.8f))
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
                            listOf(Color(zikr.color).copy(0.15f), SummerTheme.colors.cardBackground, Color(zikr.color).copy(0.15f))
                        )
                    )
                        .border(1.5.dp, Color(zikr.color).copy(0.35f), SummerTheme.Shapes.ExtraRounded)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(zikr.text, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SummerTheme.colors.textPrimary, textAlign = TextAlign.Center)
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
                    // بلا حد: نعرض دورة بصرية كل 33 بدل نسبة فعلية، لأنه مفيش هدف نقيس بيه التقدم
                    val progress = if (!isUnlimited) {
                        state.currentCount.toFloat() / zikr.target.toFloat()
                    } else {
                        (state.currentCount % 33).toFloat() / 33f
                    }
                    val animatedProgress by animateFloatAsState(progress, label = "progress")

                    Canvas(modifier = Modifier.fillMaxSize().rotate(rotation)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(zikr.color).copy(0.2f), Color(zikr.color).copy(0.04f), Color.Transparent)
                            ),
                            radius = size.width * 0.52f
                        )
                    }
                    val circleColor = SummerTheme.colors.white.copy(0.4f)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = circleColor, style = Stroke(width = 22.dp.toPx()))
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
                        if (isUnlimited) {
                            Icon(
                                imageVector = Icons.Rounded.AllInclusive,
                                contentDescription = null,
                                tint = Color(zikr.color).copy(0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("📿", fontSize = 28.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${state.currentCount}", fontSize = 72.sp, fontWeight = FontWeight.Black, color = SummerTheme.colors.textPrimary)
                        Text(
                            text = if (isUnlimited) genderedStringResource(GenderedStrings.sebhaUnlimited) else "/ ${zikr.target}",
                            fontSize = 20.sp,
                            color = Color(zikr.color).copy(0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isUnlimited && progress >= 1f) CelebrationOverlaySebha(onDismiss = onDismissCelebration)
                }

                Spacer(Modifier.height(36.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                    SummerActionButton(icon = Icons.Rounded.Refresh, color = SummerTheme.colors.textPrimary.copy(0.6f), onClick = onReset)
                    if (zikr.audioResId != 0) {
                        SummerActionButton(
                            icon = if (state.isPlayingAudio) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                            color = if (state.isPlayingAudio) Color(zikr.color) else SummerTheme.colors.textPrimary.copy(0.6f),
                            onClick = onToggleSound
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(genderedStringResource(GenderedStrings.sebhaTapToCount), color = SummerTheme.colors.textPrimary.copy(0.5f), fontSize = 13.sp, modifier = Modifier.padding(bottom = 100.dp))
            }
        }
    }
}
