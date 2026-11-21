package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun HomeSectionHeader(
    badge: HomeBadgeStyle,
    title: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeIconBadge(badge, Modifier.size(34.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = SummerTheme.typography.sectionTitle,
            color = tint
        )
    }
}
