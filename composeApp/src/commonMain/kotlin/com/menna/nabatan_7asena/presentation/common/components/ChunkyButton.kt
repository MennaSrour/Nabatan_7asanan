package com.menna.nabatan_7asena.presentation.common.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A layered button with a face that sinks onto its base when pressed. */
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
    faceBrush: Brush? = null,
    faceBorder: Color? = null,
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
            role = Role.Button,
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
                .then(if (faceBrush == null) Modifier.background(color) else Modifier.background(faceBrush))
                .then(if (faceBorder == null) Modifier else Modifier.border(1.5.dp, faceBorder, shape))
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
