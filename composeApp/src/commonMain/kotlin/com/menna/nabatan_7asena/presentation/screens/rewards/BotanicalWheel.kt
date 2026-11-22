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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.ui.theme.KidsFontFamily
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private val Emerald = Color(0xFF205C42)
private val Forest = Color(0xFF153E2E)
private val Moss = Color(0xFF80A45D)
private val Pollen = Color(0xFFFFD373)
private val Petal = Color(0xFFF9BACB)
private val Cream = Color(0xFFFFF4CE)
private val WedgeColors = listOf(
    Color(0xFF75BD93), Color(0xFFCEE3A9), Color(0xFFFFCECA), Color(0xFFF8D88A),
    Color(0xFFA3D8BB), Color(0xFFF5C76F), Color(0xFFE3D0EC)
)
private data class SeedParticle(val angle: Float, val distance: Float, val size: Float)

@Composable
internal fun BotanicalWheel(
    rotation: Float,
    celebration: Float,
    spinning: Boolean,
    enabled: Boolean,
    onSpin: () -> Unit
) {
    val font = KidsFontFamily
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = Forest, fontFamily = font, fontWeight = FontWeight.ExtraBold,
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
                Brush.radialGradient(listOf(Pollen.copy(alpha = .37f), Color.Transparent), center, radius * 1.3f),
                radius * 1.3f
            )
            drawCircle(Emerald, radius * 1.13f)
            drawCircle(Moss, radius * 1.09f, style = Stroke(2.dp.toPx()))
            drawCircle(Pollen, radius * 1.025f, style = Stroke(2.dp.toPx()))

            val active = (-rotation / wheelSlice).roundToInt()
            val tick = (1f - abs(rotation / wheelSlice + active) * 2f).coerceIn(0f, 1f)
            labels.forEachIndexed { index, label ->
                val angle = -90f + index * wheelSlice + rotation
                val isUnderPointer = ((active % labels.size) + labels.size) % labels.size == index
                drawArc(WedgeColors[index], angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2))
                if (isUnderPointer) {
                    drawArc(Cream.copy(alpha = if (spinning) .15f + .28f * tick else .2f),
                        angle - wheelSlice / 2, wheelSlice, true, topLeft, Size(radius * 2, radius * 2))
                }
                val anchor = center + direction(angle) * (radius * .69f)
                drawText(label, topLeft = anchor - Offset(label.size.width / 2f, label.size.height / 2f))
            }
            labels.indices.forEach { index ->
                val boundary = -90f + index * wheelSlice + rotation - wheelSlice / 2
                drawLine(Cream.copy(alpha = .82f), center, center + direction(boundary) * radius, 1.5.dp.toPx())
            }

            repeat(30) { index ->
                val angle = index * 12f + rotation
                val point = center + direction(angle) * (radius * 1.073f)
                if (index % 5 == 0) {
                    drawBlossom(point, radius * .044f, if (index % 2 == 0) Petal else Cream)
                } else {
                    drawLeaf(point, angle + if (index % 2 == 0) 63f else 117f,
                        radius * .079f, if (index % 2 == 0) Moss else Color(0xFFABD89C))
                    drawCircle(Pollen, radius * .011f, center + direction(angle + 5f) * (radius * 1.11f))
                }
            }

            drawCircle(
                Brush.radialGradient(listOf(Cream, Pollen.copy(alpha = .65f), Color.Transparent), center, radius * .44f),
                radius * .44f
            )
            val pointer = center + Offset(0f, -radius * (1.24f - if (spinning) tick * .036f else 0f))
            drawLeaf(pointer, 90f, radius * .34f, Pollen, Emerald)
            drawCircle(Cream, radius * .022f, pointer + Offset(0f, radius * .09f))

            if (celebration < 1f) {
                val fade = (1f - celebration)
                particles.forEachIndexed { index, particle ->
                    val point = center + direction(particle.angle) * (radius * (.28f + particle.distance * celebration)) +
                        Offset(0f, radius * celebration * celebration * .2f)
                    val scale = radius * particle.size
                    if (index % 3 == 0) {
                        drawLeaf(point, particle.angle + 210f * celebration, scale * .12f, Moss.copy(alpha = fade))
                    } else {
                        val dot = if (index % 3 == 1) Pollen else Petal
                        val flare = scale * .048f
                        drawLine(dot.copy(alpha = fade), point - Offset(flare, 0f), point + Offset(flare, 0f), 1.8.dp.toPx())
                        drawLine(dot.copy(alpha = fade), point - Offset(0f, flare), point + Offset(0f, flare), 1.8.dp.toPx())
                        drawCircle(Cream.copy(alpha = fade), scale * .013f, point)
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
                containerColor = Cream, contentColor = Forest,
                disabledContainerColor = Emerald, disabledContentColor = Cream
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.size(22.dp)) {
                    drawBlossom(center, size.minDimension * .30f, if (enabled) Moss else Petal)
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

private fun DrawScope.drawLeaf(base: Offset, angle: Float, length: Float, color: Color, vein: Color = Emerald) {
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

private fun DrawScope.drawBlossom(point: Offset, radius: Float, color: Color) {
    repeat(5) { petal ->
        drawCircle(color, radius * .55f, point + direction(petal * 72f - 90f) * radius * .62f)
    }
    drawCircle(Pollen, radius * .36f, point)
}
