package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.TaskCategory

internal data class HomeBadgeStyle(val icon: ImageVector, val tint: Color, val background: Color)

internal fun prayerBadgeStyle(name: String): HomeBadgeStyle = when (name) {
    "الفجر", "العشاء" -> HomeBadgeStyle(Icons.Rounded.DarkMode, Color(0xFF6751A6), Color(0xFFF0E8FF))
    "المغرب" -> HomeBadgeStyle(Icons.Rounded.WbTwilight, Color(0xFF9C5D22), Color(0xFFFFE9C1))
    "الظهر", "العصر" -> HomeBadgeStyle(Icons.Rounded.WbSunny, Color(0xFF94621F), Color(0xFFFFF1C7))
    else -> HomeBadgeStyle(Icons.Rounded.Mosque, Color(0xFF346C98), Color(0xFFE1F2FF))
}

internal fun taskBadgeStyle(category: TaskCategory): HomeBadgeStyle = when (category) {
    TaskCategory.QURAN -> HomeBadgeStyle(Icons.Rounded.LocalLibrary, Color(0xFF267458), Color(0xFFDCF5E9))
    TaskCategory.AZKAR -> HomeBadgeStyle(Icons.Rounded.Spa, Color(0xFF94621F), Color(0xFFFFEBCB))
    TaskCategory.EXTRA -> HomeBadgeStyle(Icons.Rounded.Stars, Color(0xFF6751A6), Color(0xFFF0E8FF))
    TaskCategory.PRAYER -> prayerBadgeStyle("")
}

@Composable
internal fun HomeIconBadge(style: HomeBadgeStyle, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(14.dp), color = style.background,
        border = BorderStroke(1.dp, style.tint.copy(alpha = 0.16f)), shadowElevation = 2.dp) {
        Box(contentAlignment = Alignment.Center) {
            Icon(style.icon, contentDescription = null, tint = style.tint, modifier = Modifier.fillMaxSize(0.6f))
        }
    }
}
