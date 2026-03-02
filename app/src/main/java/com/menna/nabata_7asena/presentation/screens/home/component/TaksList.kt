package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksList(
    tasks: List<HomeUiState.UiTaskItem>,
    onTaskClick: (HomeUiState.UiTaskItem) -> Unit,
    onPlaySound: (HomeUiState.UiTaskItem) -> Unit,
    wisdom: String,

    onTreasureClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { DailyWisdomCard(wisdom = wisdom) }

        items(items = tasks, key = { it.id }) { item ->
            Box(modifier = Modifier.animateItem()) {
                TaskBubbleCard(
                    item = item, onClick = { onTaskClick(item) },
                    onPlaySound = { onPlaySound(item) }
                )
            }
        }

        item {
            TreasureChestCard(
                isUnlocked = tasks.isNotEmpty() && tasks.all { it.isCompleted },
                onClick = onTreasureClick
            )
        }
    }
}

@Composable
fun TaskBubbleCard(
    item: HomeUiState.UiTaskItem,
    onClick: () -> Unit,
    onPlaySound: () -> Unit
) {
    val scale by animateFloatAsState(if (item.isCompleted) 0.98f else 1f, label = "scale")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                if (item.isCompleted) 0.dp else 8.dp,
                RoundedCornerShape(26.dp),
                spotColor = Color(0xFF90A4AE)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp), color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(item.backgroundColor)
                .padding(vertical = 24.dp, horizontal = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(Color.White.copy(0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 32.sp)
                }

                Spacer(Modifier.width(20.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        item.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = item.contentColor,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                    )
                    if (item.subtitle != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            item.subtitle,
                            fontSize = 15.sp,
                            color = item.contentColor.copy(0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                val icon = if (item.isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff
                val iconColor = if (item.isPlaying) Color.White else Color.White.copy(alpha = 0.5f)

                if (!item.isCompleted && item.category != TaskCategory.EXTRA) {
                    ActionButton(
                        icon = icon,
                        color = iconColor,
                        onClick = onPlaySound
                    )
                }
                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (item.isCompleted) Color(0xFF43A047) else Color.White.copy(0.3f),
                            CircleShape
                        )
                        .border(2.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) Icon(
                        Icons.Filled.Check,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyWisdomCard(wisdom: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(10.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF9575CD))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))))
            .clickable { }
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                color = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(52.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Lightbulb,
                        null,
                        tint = Color(0xFF673AB7),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(
                    "نور اليوم ✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF512DA8)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    wisdom,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4527A0),
                    lineHeight = 28.sp
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.size(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}