package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.domain.entity.TaskCategory

private data class CategoryPalette(val gradientStart: Color, val gradientEnd: Color)

private val CATEGORY_PALETTES = mapOf(
    TaskCategory.QURAN to CategoryPalette(
        gradientStart = Color(0xFF4BE0AC),
        gradientEnd = Color(0xFF1B8F6B)
    ),
    TaskCategory.CHALLENGE to CategoryPalette(
        gradientStart = Color(0xFFFFCE55),
        gradientEnd = Color(0xFFE8930A)
    ),
    TaskCategory.AZKAR to CategoryPalette(
        gradientStart = Color(0xFFFFA36B),
        gradientEnd = Color(0xFFF1552F)
    ),
    TaskCategory.EXTRA to CategoryPalette(
        gradientStart = Color(0xFF9B85E8),
        gradientEnd = Color(0xFF6A4FC7)
    ),
    TaskCategory.PRAYER to CategoryPalette(
        gradientStart = Color(0xFF5C4FA6),
        gradientEnd = Color(0xFF362B6E)
    )
)
fun categoryGradient(category: TaskCategory): Brush {
    val palette = CATEGORY_PALETTES.getValue(category)
    return Brush.verticalGradient(listOf(palette.gradientStart, palette.gradientEnd))
}

fun categoryColor(category: TaskCategory): Color = CATEGORY_PALETTES.getValue(category).gradientEnd
