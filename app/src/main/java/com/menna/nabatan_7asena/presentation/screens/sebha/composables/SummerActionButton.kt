package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SummerActionButton(icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = CircleShape,
        color = color.copy(0.12f), modifier = Modifier.size(60.dp),
        border = BorderStroke(1.5.dp, color.copy(0.35f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
    }
}
