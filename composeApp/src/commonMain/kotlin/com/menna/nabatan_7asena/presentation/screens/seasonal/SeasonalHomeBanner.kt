package com.menna.nabatan_7asena.presentation.screens.seasonal

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan.shared.generated.resources.*
import com.menna.nabatan_7asena.core.util.SeasonalMode
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.LottieConstants
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition
import com.menna.nabatan_7asena.resources.AppResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal val RamadanLavender = Color(0xFF332451)
internal val RamadanGold = Color(0xFFFFD980)

@Composable
internal fun SeasonalHomeBanner(mode: SeasonalMode, onCalendar: () -> Unit) {
    if (mode == SeasonalMode.DEFAULT) return
    val ramadan = mode == SeasonalMode.RAMADAN
    Box(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    if (ramadan) listOf(Color(0xFF533776), RamadanLavender)
                    else listOf(Color(0xFF216C69), Color(0xFF12464D))
                )
            )
    ) {
        if (ramadan) {
            SeasonalLottie(AppResources.raw.lights, Modifier.align(Alignment.TopCenter).fillMaxWidth().height(88.dp))
            SeasonalLottie(AppResources.raw.lantern_swing, Modifier.align(Alignment.TopEnd).size(84.dp))
            Row(
                Modifier.fillMaxWidth().padding(start = 18.dp, end = 16.dp, top = 46.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(Res.string.season_ramadan_greeting), color = RamadanGold,
                        style = MaterialTheme.typography.headlineSmall)
                    Button(
                        onClick = onCalendar,
                        colors = ButtonDefaults.buttonColors(containerColor = RamadanGold, contentColor = RamadanLavender)
                    ) { Text(stringResource(Res.string.ramadan_calendar_title)) }
                }
                Image(painterResource(Res.drawable.timur_ramadan), contentDescription = null,
                    modifier = Modifier.width(88.dp).height(126.dp))
            }
        } else {
            EidDecorations(Modifier.fillMaxWidth().height(152.dp))
            Text(
                stringResource(Res.string.season_eid_greeting),
                modifier = Modifier.align(Alignment.Center).padding(top = 14.dp),
                color = Color(0xFFFFE7AD), textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun SeasonalLottie(resourceId: Int, modifier: Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resourceId))
    LottieAnimation(composition, modifier, iterations = LottieConstants.IterateForever)
}

private val EidColors = listOf(Color(0xFFFFC4D8), Color(0xFFFFDF91), Color(0xFFB9AAFF), Color(0xFF9CE4CA))

@Composable
private fun EidDecorations(modifier: Modifier) {
    val drift by rememberInfiniteTransition(label = "Eid sparkles").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse), label = "drift"
    )
    Canvas(modifier) {
        val flagWidth = size.width / 12f
        fun rope(x: Float) = size.height * (0.11f + 0.32f * x * (1f - x))
        repeat(12) { index ->
            val start = index * flagWidth
            val end = start + flagWidth
            drawLine(Color.White.copy(alpha = 0.6f), Offset(start, rope(index / 12f)),
                Offset(end, rope((index + 1) / 12f)), 1.5.dp.toPx())
            drawPath(Path().apply {
                moveTo(start + 2f, rope(index / 12f))
                lineTo(end - 2f, rope((index + 1) / 12f))
                lineTo(start + flagWidth / 2f, rope((index + 0.5f) / 12f) + 15.dp.toPx())
                close()
            }, EidColors[index % EidColors.size])
        }
        listOf(0.09f, 0.16f, 0.84f, 0.91f).forEachIndexed { index, fraction ->
            val center = Offset(size.width * fraction, size.height * (0.57f + index % 2 * 0.13f) - drift * 4.dp.toPx())
            val radius = 9.dp.toPx()
            drawLine(Color.White.copy(alpha = 0.65f), center, center + Offset(3.dp.toPx(), 29.dp.toPx()))
            drawOval(EidColors[index], center - Offset(radius, radius), Size(radius * 2, radius * 2.4f))
        }
        repeat(14) { index ->
            val x = size.width * ((index * 19 % 89) + 5) / 100f
            val y = size.height * (0.38f + (index * 13 % 57) / 100f) - drift * 7.dp.toPx()
            val radius = (2 + index % 3).dp.toPx()
            val color = EidColors[index % EidColors.size].copy(alpha = 0.45f + drift * 0.35f)
            drawLine(color, Offset(x - radius, y), Offset(x + radius, y), 1.dp.toPx())
            drawLine(color, Offset(x, y - radius), Offset(x, y + radius), 1.dp.toPx())
        }
    }
}
