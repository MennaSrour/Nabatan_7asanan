package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun TaskBubbleCard(
    item: HomeUiState.UiTaskItem,
    hasBookmark: Boolean,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    onPlaySound: () -> Unit
) {
    val completeDescription = genderedStringResource(GenderedStrings.taskComplete, item.title)
    val icon = remember(item.category) { categoryIcon(item.category) }
    val baseColor = remember(item.category) { categoryColor(item.category) }
    val displaySubtitle = when {
        item.category == TaskCategory.QURAN && hasBookmark && !item.isCompleted ->
        genderedStringResource(GenderedStrings.taskResumeBookmark)
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
        // كانت باعتة toCardGradient() على لون واحد (categoryColor) وبتطلّع تدرّج
        // بمعادلة عامة — دلوقتي بتاخد التدرّج المختار يدويًا لنفس التصنيف من
        // TaskStyle.kt (نفس عيلة الألوان، لكن مصمم مش متولّد تلقائيًا)
        categoryGradient(item.category)
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
                        contentDescription = genderedStringResource(GenderedStrings.listen),
                        tint = SummerTheme.colors.white,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!item.isCompleted) {
                // كان مجرد مربع شفاف من غير أيقونة أو حدود — بصريًا مش واضح إنه
                // "تشيك بوكس" قابل للضغط. دلوقتي بقى فيه حلقة واضحة + أيقونة
                // دايرة فاضية، وده بيبقى معروف بصريًا كأي checkbox قياسي.
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SummerTheme.colors.white.copy(alpha = 0.22f))
                        .border(1.5.dp, SummerTheme.colors.white.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                        .clickable { onCheckClick() }
                        .semantics {
                        role = Role.Checkbox
                        contentDescription = completeDescription
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = SummerTheme.colors.white.copy(alpha = 0.9f),
                        modifier = Modifier.size(15.dp)
                    )
                }
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
                            contentDescription = genderedStringResource(GenderedStrings.completed),
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
