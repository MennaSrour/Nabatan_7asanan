package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun TaskBubbleCard(
    item: HomeUiState.UiTaskItem,
    hasBookmark: Boolean,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    onPlaySound: () -> Unit
) {
    val icon = remember(item.category) { categoryIcon(item.category) }
    val baseColor = remember(item.category) { categoryColor(item.category) }
    val displaySubtitle = when {
        item.category == TaskCategory.QURAN && hasBookmark && !item.isCompleted ->
            "تابعي من عند العلامة المحفوظة"
        else -> item.subtitle
    }

    val baseGradient = if (item.isCompleted) {
        Brush.verticalGradient(
            listOf(
                lerp(baseColor, SummerTheme.colors.taskCompletedBase, 0.6f),
                lerp(baseColor, SummerTheme.colors.taskCompletedBase, 0.75f)
            )
        )
    } else {
        baseColor.toCardGradient()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(baseGradient)
                .clickable { onClick() }
                .padding(horizontal = SummerTheme.dimensions.paddingLarge, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SummerTheme.colors.white.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = SummerTheme.colors.white,
                    style = SummerTheme.typography.taskTitle
                )
                if (!displaySubtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = displaySubtitle,
                        color = SummerTheme.colors.white.copy(alpha = 0.85f),
                        style = SummerTheme.typography.taskSubtitle
                    )
                }
            }

            if (item.category == TaskCategory.AZKAR && !item.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SummerTheme.colors.white.copy(alpha = 0.25f))
                        .clickable { onPlaySound() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                        contentDescription = "استمعي",
                        tint = SummerTheme.colors.white,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!item.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(SummerTheme.colors.white.copy(alpha = 0.28f))
                        .clickable { onCheckClick() }
                )
            }
        }

        AnimatedVisibility(
            visible = item.isCompleted,
            enter = fadeIn() + scaleIn(
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SummerTheme.colors.white.copy(alpha = 0.22f))
                    .clickable { onCheckClick() },
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.padding(end = SummerTheme.dimensions.paddingLarge),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SummerTheme.colors.white),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "تمت",
                            tint = baseColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun categoryIcon(category: TaskCategory): ImageVector = when (category) {
    TaskCategory.QURAN -> Icons.Rounded.MenuBook
    TaskCategory.AZKAR -> Icons.Rounded.SelfImprovement
    TaskCategory.CHALLENGE -> Icons.Rounded.EmojiEvents
    TaskCategory.EXTRA -> Icons.Rounded.AutoAwesome
    TaskCategory.PRAYER -> Icons.Rounded.AutoAwesome
}