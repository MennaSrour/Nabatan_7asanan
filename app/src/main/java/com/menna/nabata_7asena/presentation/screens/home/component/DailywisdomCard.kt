package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun DailyWisdomCard(wisdom: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SummerTheme.colors.wisdomStart)
            .padding(SummerTheme.dimensions.paddingLarge)
    ) {
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = "نور اليوم",
                style = SummerTheme.typography.wisdomTitle,
                color = SummerTheme.colors.wisdomTextPrimary
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = wisdom,
                style = SummerTheme.typography.wisdomBody,
                color = SummerTheme.colors.wisdomTextSecondary
            )
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(90.dp)
                .padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            ClayAssetImage(
                resName = "daily_light",
                fallbackIcon = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = SummerTheme.colors.wisdomAccent,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}