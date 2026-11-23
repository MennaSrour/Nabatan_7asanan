package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.ui.theme.KidsFontFamily
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class WheelPalette(
    val emerald: Color,
    val forest: Color,
    val moss: Color,
    val pollen: Color,
    val petal: Color,
    val cream: Color,
    val wedges: List<Color>
)
private data class SeedParticle(val angle: Float, val distance: Float, val size: Float)

@Composable
internal fun BotanicalWheel(
    rotation: Float,
    celebration: Float,
    winner: WheelPrize?,
    spinning: Boolean,
    enabled: Boolean,
    onSpin: () -> Unit
) {
    val theme = SummerTheme.colors
    val palette = WheelPalette(
        emerald = theme.successGreen,
        forest = theme.textDarkBrown,
        moss = theme.leafGreen,
        pollen = theme.goldWarm,
        petal = theme.secondaryAccent,
        cream = theme.dialogBackground,
        wedges = listOf(
            theme.successGreen, theme.wisdomStart, theme.secondaryAccent.copy(alpha = .72f),
            theme.goldWarm.copy(alpha = .72f), theme.leafGreen.copy(alpha = .68f),
            theme.wisdomAccent.copy(alpha = .68f), theme.goldDeep.copy(alpha = .55f)
        )
    )
    val font = KidsFontFamily
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = palette.forest, fontFamily = font, fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp, lineHeight = 15.sp, textAlign = TextAlign.Center
    )
    val labels = remember(measurer, labelStyle) {
        WheelPrize.segments.map { prize ->
            val lines = when (prize) {
                WheelPrize.CATCH -> "التقاط\nالحسنات"
                WheelPrize.MEMORY -> "لعبة\nالذاكرة"
                WheelPrize.TRIVIA -> "تحدي\nالسرعة"
                WheelPrize.WATER_3 -> "٣ قطرات\nماء"
                else -> prize.label.replaceFirst(' ', '\n')
            }
            measurer.measure(lines, labelStyle)
        }
    }
    val particles = remember {
        val random = Random(67)
        List(42) { index ->
            SeedParticle(index * (360f / 42) + random.nextFloat() * 15f,
                .68f + random.nextFloat() * .67f, .55f + random.nextFloat() * .75f)
        }
    }

    Box(Modifier.widthIn(max = 378.dp).fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val radius = size.minDimension * .38f
            val topLeft = center - Offset(radius, radius)
            drawCircle(
                Brush.radialGradient(listOf(palette.pollen.copy(alpha = .37f), Color.Transparent), center, radius * 1.3f),
                radius * 1.3f
            )
            drawCircle(palette.emerald, radius * 1.13f)
            drawCircle(palette.moss, radius * 1.09f, style = Stroke(2.dp.toPx()))
            drawCircle(palette.pollen, radius * 1.025f, style = Stroke(2.dp.toPx()))

            val active = (-rotation / wheelSlice).roundToInt()
            val segment = rotation / wheelSlice + .5f
            val tickPhase = segment - floor(segment)
            val bounce = (1f - tickPhase / .36f).coerceIn(0f, 1f)
            val pulse = (1f - celebration) * .28f
            labels.forEachIndexed { index, label ->
                val angle = -90f + index * wheelSlice + rotation
                val isUnderPointer = ((active % labels.size) + labels.size) % labels.size == index
                drawArc(palette.wedges[index], angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2))
                if (WheelPrize.segments[index] == winner) {
                    drawArc(
                        Brush.radialGradient(listOf(palette.cream.copy(alpha = .34f), palette.pollen.copy(alpha = .74f + pulse * .5f)),
                            center, radius),
                        angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2)
                    )
                    drawArc(palette.cream, angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2),
                        style = Stroke(2.5.dp.toPx()))
                } else if (spinning && isUnderPointer) {
                    drawArc(palette.cream.copy(alpha = .13f + .12f * bounce),
                        angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2))
                }
                val anchor = center + direction(angle) * (radius * .69f)
                drawText(label, topLeft = anchor - Offset(label.size.width / 2f, label.size.height / 2f))
            }
            labels.indices.forEach { index ->
                val boundary = -90f + index * wheelSlice + rotation - wheelSlice / 2
                drawLine(palette.cream.copy(alpha = .82f), center, center + direction(boundary) * radius, 1.5.dp.toPx())
            }

            repeat(30) { index ->
                val angle = index * 12f + rotation
                val point = center + direction(angle) * (radius * 1.073f)
                if (index % 5 == 0) {
                    drawBlossom(point, radius * .044f, if (index % 2 == 0) palette.petal else palette.cream, palette.pollen)
                } else {
                    drawLeaf(point, angle + if (index % 2 == 0) 63f else 117f,
                        radius * .079f, if (index % 2 == 0) palette.moss else palette.moss.copy(alpha = .55f), palette.emerald)
                    drawCircle(palette.pollen, radius * .011f, center + direction(angle + 5f) * (radius * 1.11f))
                }
            }

            drawCircle(
                Brush.radialGradient(listOf(palette.cream, palette.pollen.copy(alpha = .65f), Color.Transparent), center, radius * .44f),
                radius * .44f
            )
            val tickLift = if (spinning) bounce * bounce else 0f
            val pointer = center + Offset(0f, -radius * (1.24f + tickLift * .024f))
            rotate(-10f * tickLift, pointer) {
                drawLeaf(pointer, 90f, radius * .34f, palette.pollen, palette.emerald)
                drawCircle(palette.cream, radius * .022f, pointer + Offset(0f, radius * .09f))
            }

            if (celebration < 1f) {
                val fade = (1f - celebration)
                particles.forEachIndexed { index, particle ->
                    val point = center + direction(particle.angle) * (radius * (.28f + particle.distance * celebration)) +
                        Offset(0f, radius * celebration * celebration * .2f)
                    val scale = radius * particle.size
                    if (index % 3 == 0) {
                        drawLeaf(point, particle.angle + 210f * celebration, scale * .12f, palette.moss.copy(alpha = fade), palette.emerald)
                    } else {
                        val dot = if (index % 3 == 1) palette.pollen else palette.petal
                        val flare = scale * .048f
                        drawPath(Path().apply {
                            repeat(10) { spike ->
                                val tip = point + direction(spike * 36f + celebration * 120f) *
                                    (flare * if (spike % 2 == 0) 1.4f else .57f)
                                if (spike == 0) moveTo(tip.x, tip.y) else lineTo(tip.x, tip.y)
                            }
                            close()
                        }, dot.copy(alpha = fade))
                    }
                }
            }
        }
        Button(
            onClick = onSpin,
            enabled = enabled,
            shape = CircleShape,
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.size(85.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.cream, contentColor = palette.forest,
                disabledContainerColor = palette.emerald, disabledContentColor = palette.cream
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.size(22.dp)) {
                    drawBlossom(center, size.minDimension * .30f, if (enabled) palette.moss else palette.petal, palette.pollen)
                }
                Text("لف العجلة", fontFamily = font, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

private fun direction(angle: Float): Offset {
    val radians = angle * PI / 180
    return Offset(cos(radians).toFloat(), sin(radians).toFloat())
}

private fun DrawScope.drawLeaf(base: Offset, angle: Float, length: Float, color: Color, vein: Color) {
    val forward = direction(angle) * length
    val side = direction(angle + 90f) * (length * .45f)
    val tip = base + forward
    val middle = base + forward * .47f
    drawPath(Path().apply {
        moveTo(base.x, base.y)
        quadraticTo((middle + side).x, (middle + side).y, tip.x, tip.y)
        quadraticTo((middle - side).x, (middle - side).y, base.x, base.y)
        close()
    }, color)
    drawLine(vein.copy(alpha = color.alpha * .5f), base + forward * .13f, base + forward * .82f,
        (length * .045f).coerceAtLeast(.8f))
}

private fun DrawScope.drawBlossom(point: Offset, radius: Float, color: Color, centerColor: Color) {
    repeat(5) { petal ->
        drawCircle(color, radius * .55f, point + direction(petal * 72f - 90f) * radius * .62f)
    }
    drawCircle(centerColor, radius * .36f, point)
}
