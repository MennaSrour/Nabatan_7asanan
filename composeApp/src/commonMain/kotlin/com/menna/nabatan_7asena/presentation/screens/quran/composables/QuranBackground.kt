package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun QuranBackground() {
    val q = SummerTheme.quran
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    q.backgroundTop,
                    q.backgroundMid,
                    q.backgroundEnd
                )
            )
        )
    }
}
