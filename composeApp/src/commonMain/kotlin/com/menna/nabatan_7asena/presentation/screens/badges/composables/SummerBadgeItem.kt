package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.badges.Badge
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesUiState
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton

internal val TrophyInk = Color(0xFF3E2859)

@Composable
internal fun SummerBadgeItem(badge: Badge, stats: BadgesUiState, onClick: () -> Unit) {
    val earned = badge.isUnlocked(stats)
    val top = if (earned) Color(0xFFFFF4C6) else Color(0xFFF4EEFC)
    val bottom = if (earned) lerp(Color.White, badge.tint, .48f)
        else Color(0xFFE5DDF2)
    ChunkyButton(
        onClick = onClick,
        color = top,
        edgeColor = if (earned) Color(0xFFCB913D) else Color(0xFFB5A7C7),
        faceBorder = if (earned) Color(0xFFFFEAA8) else Color(0xFFFDF9FF),
        faceBrush = Brush.verticalGradient(listOf(top, bottom)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().heightIn(min = 152.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            MedalArtwork(badge, earned, Modifier.size(76.dp))
            Text(badge.title, color = TrophyInk, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(if (earned) "وسام مكتسب" else badge.goal, color = TrophyInk,
                style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            if (!earned) MedalProgress(badge.fraction(stats), badge.tint, Modifier.height(8.dp))
        }
    }
}

@Composable
internal fun MedalProgress(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    val animated = remember { Animatable(0f) }
    LaunchedEffect(fraction) { animated.animateTo(fraction, tween(750)) }
    val shape = RoundedCornerShape(50)
    Box(modifier.fillMaxWidth().progressSemantics(fraction).clip(shape).background(Color(0xFFDBCEE9))) {
        Box(
            Modifier.fillMaxWidth(animated.value).fillMaxHeight().clip(shape).background(
                Brush.verticalGradient(listOf(lerp(color, Color.White, .25f), color))
            )
        )
    }
}

@Composable
internal fun MedalArtwork(badge: Badge, earned: Boolean, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val unit = size.minDimension / 100f
            val origin = center - Offset(50 * unit, 50 * unit)
            fun point(x: Int, y: Int) = origin + Offset(x * unit, y * unit)
            val ribbonColor = if (earned) badge.tint else Color(0xFFB6A9CD)
            for (x in listOf(27, 54)) {
                val ribbon = Path().apply {
                    for ((index, corner) in listOf(point(x, 9), point(x + 20, 9), point(x + 16, 51), point(x + 2, 51)).withIndex()) {
                        if (index == 0) moveTo(corner.x, corner.y) else lineTo(corner.x, corner.y)
                    }
                    close()
                }
                drawPath(ribbon, Brush.verticalGradient(listOf(ribbonColor, TrophyInk), startY = point(x, 9).y, endY = point(x, 74).y))
            }
            drawCircle(if (earned) Color(0xFFFFE6A7).copy(alpha = .65f) else Color(0xFFD5C8E5), 37 * unit, point(50, 58))
            drawCircle(if (earned) Color(0xFFB97C35) else Color(0xFF9789B1), 33 * unit, point(50, 61))
            drawCircle(
                Brush.linearGradient(
                    if (earned) listOf(Color(0xFFFFF0A0), Color(0xFFE9A944)) else listOf(Color.White, Color(0xFFAC9CC8)),
                    point(22, 34), point(80, 80)
                ),
                33 * unit, point(50, 56)
            )
            drawCircle(if (earned) Color(0xFFFFE59B) else Color(0xFFEDE6F7), 24 * unit, point(50, 55))
            if (earned) {
                drawLine(Color.White, point(67, 31), point(79, 31), 2 * unit)
                drawLine(Color.White, point(73, 25), point(73, 37), 2 * unit)
            }
        }
        BoxWithConstraints(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            val emblemSize = maxWidth * .34f
            val emblemModifier = Modifier.offset(y = maxHeight * .045f)
            if (earned) Text(badge.emblem, modifier = emblemModifier, fontSize = (emblemSize.value).sp, textAlign = TextAlign.Center)
            else Icon(Icons.Rounded.Lock, contentDescription = "وسام مغلق", tint = TrophyInk,
                modifier = emblemModifier.size(emblemSize * .72f))
        }
    }
}
