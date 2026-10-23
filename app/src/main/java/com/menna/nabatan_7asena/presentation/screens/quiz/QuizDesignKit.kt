package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.KidsFontFamily
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * ─────────────────────────────────────────────────────────────
 * "أدوات الديزاين" المشتركة لفيتشر المسابقة.
 *
 * الفكرة: بدل كروت مسطحة بحدود رفيعة، كل حاجة هنا "لعبة":
 *  - أزرار سميكة ليها "حتة تحت" (زي الأزرار البلاستيك) بتنزل لما تدوسي
 *  - ألوان حلوى ثابتة (مش مربوطة بالثيم عشان تفضل مبهجة نهار وليل)
 *  - ستيكرز مايلة شوية، وشعاع شمس بيلف، وكونفيتي
 * ─────────────────────────────────────────────────────────────
 */
internal object Candy {
    val Violet = Color(0xFF8E6FE0)
    val VioletEdge = Color(0xFF6A4BC0)
    val Sky = Color(0xFF3FA7E0)
    val SkyEdge = Color(0xFF2582B8)
    val Orange = Color(0xFFF59A23)
    val OrangeEdge = Color(0xFFC9760B)
    val Pink = Color(0xFFEC6FA8)
    val PinkEdge = Color(0xFFC24D86)
    val Leaf = Color(0xFF3DAE7C)
    val LeafEdge = Color(0xFF248A5C)
    val Coral = Color(0xFFFF7A59)
    val CoralEdge = Color(0xFFD9573A)
    val Gold = Color(0xFFFFC83D)
    val GoldEdge = Color(0xFFE59A00)
    val Wrong = Color(0xFFE53935)
    val InkBrown = Color(0xFF3E2723)

    /**
     * ألوان الاختيارات — مقصود إنها مفيهاش أخضر ولا أحمر، عشان اللونين دول
     * معناهم "صح/غلط" ومينفعش الطفل يفتكر إن لون الزرار نفسه هو الإجابة.
     */
    val choicePairs = listOf(
        Violet to VioletEdge,
        Sky to SkyEdge,
        Orange to OrangeEdge,
        Pink to PinkEdge
    )

    val confetti = listOf(Coral, Sky, Violet, Pink, Orange, Leaf, Gold)
}

/** ظل خفيف لأي نص أبيض فوق زرار ملوّن عشان يفضل مقروء. */
internal val ButtonTextShadow = Shadow(
    color = Color(0x55000000),
    offset = Offset(0f, 3f),
    blurRadius = 4f
)

/** نص بخط الأطفال — بدل ما كل Text يفتكر يحط fontFamily لوحده. */
@Composable
internal fun KidsText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = SummerTheme.colors.textDarkBrown,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    shadow: Shadow? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = KidsFontFamily,
        textAlign = textAlign,
        maxLines = maxLines,
        lineHeight = lineHeight,
        textDecoration = textDecoration,
        style = LocalTextStyle.current.copy(shadow = shadow)
    )
}

/**
 * كليك بينط شوية لما تدوسي عليه (scale down لحظة اللمس، ورجوع بـ spring
 * فيه إحساس "زرار حقيقي"). مش private عمدًا عشان باقي شاشات الفيتشر
 * (نفس الـ package) تستخدمها.
 */
fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 900f),
        label = "bouncy_scale"
    )
    this
        .scale(scale)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            onClick = onClick
        )
}

/**
 * ظهور "بوب" — العنصر بيطلع من تحت بتكبير بسيط وارتداد، وممكن نأخّره
 * (delayMillis) عشان العناصر تطلع ورا بعض بدل ما الشاشة تظهر مرة واحدة.
 */
internal fun Modifier.popIn(delayMillis: Int = 0): Modifier = composed {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }
    graphicsLayer {
        val s = 0.85f + 0.15f * progress.value
        scaleX = s
        scaleY = s
        alpha = progress.value.coerceIn(0f, 1f)
        translationY = (1f - progress.value) * 40f
    }
}

/**
 * زرار "بلاستيك سميك": وش ملوّن + حتة غامقة تحته (depth). لما تدوسي،
 * الوش بينزل فوق الحتة دي كأنك ضغطتي زرار حقيقي.
 * الوش بياخد عرض الأب كله — حددي العرض من برّه (fillMaxWidth/width).
 */
@Composable
internal fun ChunkyButton(
    onClick: () -> Unit,
    color: Color,
    edgeColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(22.dp),
    depth: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    onClickLabel: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sink by animateDpAsState(
        targetValue = if (pressed) depth else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 1200f),
        label = "chunky_sink"
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClickLabel = onClickLabel,
                onClick = onClick
            )
            .padding(bottom = depth)
    ) {
        // الحتة الغامقة اللي تحت
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depth)
                .clip(shape)
                .background(edgeColor)
        )
        // الوش
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = sink)
                .clip(shape)
                .background(color)
                .drawBehind {
                    // لمعة صغيرة فوق الزرار، تدّي إحساس "بلاستيك لامع"
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.28f),
                        topLeft = Offset(size.width * 0.08f, 5.dp.toPx()),
                        size = Size(size.width * 0.84f, 4.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/** ستيكر صغير مايل شوية — بدل العناوين الجافة. */
@Composable
internal fun StickerLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    rotation: Float = -3f,
    textColor: Color = Color.White
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .rotate(rotation)
            .clip(shape)
            .background(color)
            .border(2.dp, Color.White.copy(alpha = 0.9f), shape)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        KidsText(
            text = text,
            fontSize = 13.sp,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            shadow = ButtonTextShadow
        )
    }
}

/** أشعة شمس بتلف حوالين مركز الشاشة — للعد التنازلي وشاشة النتيجة. */
@Composable
internal fun SunburstRays(
    modifier: Modifier = Modifier,
    color: Color,
    rotation: Float,
    rayCount: Int = 16
) {
    Canvas(modifier = modifier) {
        val c = center
        val radius = maxOf(size.width, size.height) * 1.2f
        val step = 360f / rayCount
        for (i in 0 until rayCount step 2) {
            val a1 = Math.toRadians((rotation + i * step).toDouble())
            val a2 = Math.toRadians((rotation + (i + 1) * step).toDouble())
            val path = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + radius * cos(a1).toFloat(), c.y + radius * sin(a1).toFloat())
                lineTo(c.x + radius * cos(a2).toFloat(), c.y + radius * sin(a2).toFloat())
                close()
            }
            drawPath(path, color)
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val speed: Int,
    val phase: Float,
    val size: Float,
    val color: Color,
    val round: Boolean
)

/** كونفيتي بينزل من فوق في حلقة مستمرة (animation واحد بس لكل القطع). */
@Composable
internal fun ConfettiRain(modifier: Modifier = Modifier, count: Int = 26) {
    val pieces = remember {
        List(count) {
            ConfettiPiece(
                x = Random.nextFloat(),
                speed = if (it % 3 == 0) 2 else 1,
                phase = Random.nextFloat(),
                size = 6f + Random.nextFloat() * 7f,
                color = Candy.confetti[it % Candy.confetti.size],
                round = it % 2 == 0
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "confetti")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "confetti_t"
    )

    Canvas(modifier = modifier) {
        pieces.forEach { p ->
            val progress = (t * p.speed + p.phase) % 1f
            val y = progress * (size.height + 40.dp.toPx()) - 20.dp.toPx()
            val sway = sin((t * p.speed + p.phase) * 4f * PI.toFloat()) * 14.dp.toPx()
            val x = p.x * size.width + sway
            val s = p.size.dp.toPx()
            if (p.round) {
                drawCircle(p.color, radius = s / 2f, center = Offset(x, y))
            } else {
                drawRoundRect(
                    color = p.color,
                    topLeft = Offset(x, y),
                    size = Size(s, s * 1.5f),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    }
}

/**
 * خلفية "حديقة" — فقاعات ألوان كبيرة ناعمة + حاجات حلوة عايمة (فراشة،
 * وردة، سحابة...) بدل النجوم بس. مش private عشان تتستخدم في كل شاشات الفيتشر.
 */
@Composable
fun LivelyBackgroundDecor(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val w = maxWidth
        val h = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                Candy.Violet.copy(alpha = 0.10f),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.9f, size.height * 0.05f)
            )
            drawCircle(
                Candy.Pink.copy(alpha = 0.08f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.05f, size.height * 0.55f)
            )
            drawCircle(
                Candy.Orange.copy(alpha = 0.10f),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * 0.98f)
            )
        }

        val decorations = remember {
            listOf(
                Triple("☁️", 0.06f, 0.16f),
                Triple("🦋", 0.84f, 0.10f),
                Triple("🌼", 0.12f, 0.80f),
                Triple("✨", 0.90f, 0.58f),
                Triple("🐞", 0.52f, 0.92f),
                Triple("🌈", 0.04f, 0.46f)
            )
        }
        decorations.forEachIndexed { i, (emoji, fx, fy) ->
            val floatOffset = SummerTheme.rememberFloatingAnimation(
                range = 6f + (i % 3) * 2f,
                durationMillis = 1800 + i * 250
            )
            Text(
                emoji,
                fontSize = (18 + (i % 3) * 5).sp,
                modifier = Modifier
                    .offset(x = w * fx, y = h * fy + floatOffset.dp)
                    .graphicsLayer { alpha = 0.85f }
            )
        }
    }
}
