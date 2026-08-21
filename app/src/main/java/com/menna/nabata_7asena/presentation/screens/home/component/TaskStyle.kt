package com.menna.nabata_7asena.presentation.screens.home.component


import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.menna.nabata_7asena.domain.entity.TaskCategory

fun categoryColor(category: TaskCategory): Color = when (category) {
    TaskCategory.QURAN -> Color(0xFF26A69A)
    TaskCategory.CHALLENGE -> Color(0xFF42A5F5)
    TaskCategory.AZKAR -> Color(0xFFFF7043)
    TaskCategory.EXTRA -> Color(0xFF7E57C2)
    TaskCategory.PRAYER -> Color(0xFF0E6EBB)
}

fun Color.toCardGradient(): Brush {
    val lighter = Color(
        red = (red + (1f - red) * 0.28f).coerceIn(0f, 1f),
        green = (green + (1f - green) * 0.28f).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * 0.28f).coerceIn(0f, 1f),
        alpha = alpha
    )
    val darker = Color(
        red = (red * 0.72f).coerceIn(0f, 1f),
        green = (green * 0.72f).coerceIn(0f, 1f),
        blue = (blue * 0.72f).coerceIn(0f, 1f),
        alpha = alpha
    )
    return Brush.verticalGradient(listOf(lighter, darker))
}