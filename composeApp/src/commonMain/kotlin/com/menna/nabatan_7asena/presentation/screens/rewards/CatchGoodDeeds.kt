package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val CatchTime = 30f
private const val BasketY = .88f
private const val BasketHalf = .125f

private data class FallingItem(val x: Float, val y: Float, val star: Boolean)

@Composable
internal fun ColumnScope.CatchGoodDeeds(onFinish: (Int, Int) -> Unit) {
    var basket by remember { mutableFloatStateOf(.5f) }
    var falling by remember { mutableStateOf(emptyList<FallingItem>()) }
    var seconds by remember { mutableIntStateOf(CatchTime.toInt()) }
    var score by remember { mutableIntStateOf(0) }
    val currentFinish by rememberUpdatedState(onFinish)
    fun move(target: Float) { basket = target.coerceIn(BasketHalf, 1f - BasketHalf) }

    LaunchedEffect(Unit) {
        var lastFrame = withFrameNanos { it }
        var elapsed = 0f
        var nextSpawn = .25f
        while (elapsed < CatchTime) {
            val frame = withFrameNanos { it }
            val step = ((frame - lastFrame) / 1_000_000_000f).coerceIn(0f, .05f)
            lastFrame = frame
            elapsed += step
            seconds = (CatchTime - elapsed).coerceAtLeast(0f).let { kotlin.math.ceil(it).toInt() }
            if (elapsed >= nextSpawn) {
                falling = falling + FallingItem(.08f + Random.nextFloat() * .84f, -.05f, Random.nextInt(4) != 0)
                nextSpawn += (.77f - elapsed * .006f)
            }
            falling = falling.mapNotNull { item ->
                val nextY = item.y + step * (.32f + elapsed * .004f)
                if (item.y < BasketY && nextY >= BasketY && abs(item.x - basket) < BasketHalf) {
                    score = (score + if (item.star) 1 else -1).coerceAtLeast(0)
                    null
                } else if (nextY > 1.07f) null else item.copy(y = nextY)
            }
        }
        currentFinish(((score + 1) / 2).coerceAtMost(10), score)
    }

    RewardText("الوقت ${arabicNumber(seconds)} ث  ·  الحسنات ${arabicNumber(score)} ★", bold = true)
    RoundProgress(seconds / CatchTime)
    val fieldBrush = Brush.verticalGradient(listOf(RewardLavender, RewardCream, RewardMint))
    val prizeColor = RewardGold
    val outlineColor = RewardPurple
    Canvas(
        Modifier.fillMaxWidth().weight(1f).clip(RewardShape)
            .semantics { contentDescription = "ملعب التقاط النجوم. اسحب السلة أو استخدم أزرار اليمين واليسار." }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    move(down.position.x / size.width)
                    down.consume()
                    do {
                        val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                        move(change.position.x / size.width)
                        change.consume()
                    } while (change.pressed)
                }
            }
    ) {
        drawRect(fieldBrush)
        val unit = size.minDimension
        falling.forEach { item ->
            val at = Offset(item.x * size.width, item.y * size.height)
            if (item.star) {
                val path = Path()
                repeat(10) { i ->
                    val angle = i * PI / 5 - PI / 2
                    val radius = unit * if (i % 2 == 0) .037f else .017f
                    val x = at.x + cos(angle).toFloat() * radius
                    val y = at.y + sin(angle).toFloat() * radius
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, prizeColor)
                drawPath(path, outlineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
            } else {
                drawCircle(outlineColor, unit * .022f, at + Offset(-unit * .02f, 0f))
                drawCircle(outlineColor, unit * .028f, at + Offset(0f, -unit * .01f))
                drawCircle(outlineColor, unit * .021f, at + Offset(unit * .024f, 0f))
            }
        }
        val left = (basket - BasketHalf) * size.width
        val top = BasketY * size.height
        val width = BasketHalf * 2 * size.width
        drawRoundRect(outlineColor, Offset(left, top), Size(width, size.height * .073f), CornerRadius(9.dp.toPx()))
        drawRoundRect(prizeColor, Offset(left, top), Size(width, size.height * .024f), CornerRadius(6.dp.toPx()))
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        RewardButton("يمين", { move(basket + .14f) }, Modifier.weight(1f))
        RewardButton("يسار", { move(basket - .14f) }, Modifier.weight(1f))
    }
}
