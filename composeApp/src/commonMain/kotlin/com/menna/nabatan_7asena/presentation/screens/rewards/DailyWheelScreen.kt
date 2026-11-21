package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.KidsGame
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.ui.theme.KidsFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val wheelColors = listOf(RewardGold, RewardMint, Color(0xFFD8B7F1), RewardRose, Color(0xFFFFE5A6), Color(0xFFCEE8F5))
private const val Slice = 60f

@Composable
internal fun DailyWheelScreen(
    onBack: () -> Unit,
    onGames: () -> Unit,
    onBonus: () -> Unit,
    viewModel: KidsRewardsViewModel
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val spin by viewModel.spin.collectAsStateWithLifecycle()
    val rotation = remember { Animatable(0f) }
    val sparkles = remember { Animatable(1f) }
    val won = (spin as? SpinState.Won)?.prize
    val prize = if (spin == SpinState.Saving) null else won ?: status?.prize
    var animationFinished by remember { mutableStateOf(false) }
    val revealed = won == null || animationFinished

    LaunchedEffect(won) {
        if (won != null) {
            rotation.animateTo(1800f - won.ordinal * Slice, tween(3000, easing = CubicBezierEasing(.12f, .78f, .18f, 1f)))
            animationFinished = true
            sparkles.snapTo(0f)
            sparkles.animateTo(1f, tween(1450, easing = LinearEasing))
            viewModel.dismissSpin()
        } else {
            sparkles.snapTo(1f)
            animationFinished = false
            rotation.snapTo(0f)
        }
    }

    RewardsPage("عجلة المكافآت اليومية", onBack = { viewModel.dismissSpin(); onBack() }) {
        RewardText("لفة مجانية واحدة كل يوم", size = 21)
        Wheel(if (won != null) rotation.value else -(prize?.ordinal ?: 0) * Slice, sparkles.value)
        when {
            revealed && prize != null -> Surface(color = RewardGold, shape = RewardShape) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    RewardText("هدية اليوم · ${prize.label}", size = 24, bold = true)
                    RewardText(
                        when {
                            prize == WheelPrize.BONUS_ROUND -> "جولة إضافية في لعبة الذاكرة"
                            status?.earnedHere != true -> "سُجّلت لفة اليوم على جهاز آخر"
                            prize.water > 0 && status?.waterDelivered != true -> "جارٍ إضافة القطرات إلى حديقتك"
                            prize.water > 0 -> "يمكن استخدام القطرات لري شجرتك"
                            else -> "النجوم في رصيدك"
                        }
                    )
                }
            }
            spin == SpinState.Failed -> RewardText("تعذّر حفظ الهدية. حاول مرة أخرى.")
            spin == SpinState.Saving || status?.spinPending == true -> RewardText("جارٍ تجهيز الهدية…")
            won != null -> RewardText("العجلة تدور…")
        }
        if (prize == null) {
            RewardButton("أدر العجلة", viewModel::spinWheel, Modifier.fillMaxWidth(),
                enabled = status?.spinPending == false && spin != SpinState.Saving, color = RewardGold)
        } else if (revealed && prize == WheelPrize.BONUS_ROUND) {
            RewardButton(KidsGame.BONUS.title, { viewModel.dismissSpin(); onBonus() },
                Modifier.fillMaxWidth(), color = RewardGold)
        }
        RewardButton("ألعاب نباتا حسنا", { viewModel.dismissSpin(); onGames() },
            Modifier.fillMaxWidth(), enabled = revealed)
    }
}

@Composable
private fun Wheel(rotation: Float, celebration: Float) {
    val measurer = rememberTextMeasurer()
    val font = KidsFontFamily
    val labelStyle = TextStyle(color = RewardInk, fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    val labels = remember(measurer, labelStyle) { WheelPrize.entries.map { measurer.measure(it.label, labelStyle) } }
    val particles = remember {
        val random = Random(29)
        List(46) { Triple(random.nextFloat(), random.nextFloat(), random.nextFloat()) }
    }
    Canvas(
        Modifier.widthIn(max = 350.dp).fillMaxWidth().aspectRatio(1f)
            .semantics { contentDescription = "عجلة بست جوائز من النجوم وقطرات الماء وجولة خاصة" }
    ) {
        val radius = size.minDimension * .425f
        val diameter = radius * 2f
        val topLeft = center - Offset(radius, radius)
        val outline = 5.dp.toPx()
        WheelPrize.entries.forEachIndexed { index, _ ->
            val angle = -90f + index * Slice + rotation
            drawArc(wheelColors[index], angle - Slice / 2, Slice, true, topLeft, Size(diameter, diameter))
            val radians = angle * PI / 180
            val edge = center + Offset(cos(radians - PI / 6).toFloat(), sin(radians - PI / 6).toFloat()) * radius
            drawLine(RewardCream, center, edge, strokeWidth = 2.dp.toPx())
            val anchor = center + Offset(cos(radians).toFloat(), sin(radians).toFloat()) * (radius * .65f)
            val label = labels[index]
            drawText(label, topLeft = anchor - Offset(label.size.width / 2f, label.size.height / 2f))
        }
        drawCircle(RewardGold, radius, style = Stroke(outline))
        drawCircle(RewardCream, radius * .17f)
        drawCircle(RewardGold, radius * .10f)
        val tipY = topLeft.y + radius * .16f
        drawPath(Path().apply {
            moveTo(center.x - radius * .12f, topLeft.y - radius * .12f)
            lineTo(center.x + radius * .12f, topLeft.y - radius * .12f)
            lineTo(center.x, tipY)
            close()
        }, RewardPurple)
        if (celebration < 1f) {
            particles.forEachIndexed { index, (x, y, drift) ->
                val point = Offset(size.width * (x + (drift - .5f) * celebration * .18f),
                    size.height * (y * .6f - .14f + celebration * .64f))
                drawCircle(wheelColors[index % wheelColors.size].copy(alpha = 1f - celebration),
                    (2.5f + drift * 2.5f).dp.toPx(), point)
            }
        }
    }
}
