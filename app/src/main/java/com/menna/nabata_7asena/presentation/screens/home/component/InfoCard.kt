package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun BentoStatsGrid(user: HomeUiState.UiUser?, modifier: Modifier = Modifier) {
    val streakDigits = user?.streakText?.filter { it.isDigit() }?.ifEmpty { "0" } ?: "0"
    val starsDigits = user?.starsText?.filter { it.isDigit() }?.ifEmpty { "0" } ?: "0"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            gradient = listOf(SummerTheme.colors.statGreenStart, SummerTheme.colors.statGreenEnd),
            iconAsset = "daily_tracker_planet",
            fallbackIcon = Icons.Rounded.LocalFlorist,
            valueText = streakDigits,
            label1 = "يوم",
            label2 = "استمرارية"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            gradient = listOf(SummerTheme.colors.statPurpleStart, SummerTheme.colors.statPurpleEnd),
            iconAsset = "star_points",
            fallbackIcon = Icons.Rounded.Star,
            valueText = starsDigits,
            label1 = "نجمة",
            label2 = "إجمالي النجوم"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    gradient: List<Color>,
    iconAsset: String,
    fallbackIcon: ImageVector,
    valueText: String,
    label1: String,
    label2: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(gradient))
            .padding(SummerTheme.dimensions.paddingLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = valueText,
                    style = SummerTheme.typography.statValue,
                    color = SummerTheme.colors.white
                )
                Text(
                    text = label1,
                    style = SummerTheme.typography.statLabelPrimary,
                    color = SummerTheme.colors.white.copy(alpha = 0.9f)
                )
                Text(
                    text = label2,
                    style = SummerTheme.typography.statLabelSecondary,
                    color = SummerTheme.colors.white.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                ClayAssetImage(
                    resName = iconAsset,
                    fallbackIcon = fallbackIcon,
                    contentDescription = null,
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}