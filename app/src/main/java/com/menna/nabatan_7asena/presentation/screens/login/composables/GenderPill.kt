package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun GenderPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) SummerTheme.colors.secondaryAccent.copy(alpha = 0.16f) else SummerTheme.colors.cardBackground,
        border = BorderStroke(
            2.dp,
            if (isSelected) SummerTheme.colors.secondaryAccent else SummerTheme.colors.chipUnselected
        ),
        modifier = Modifier
            .height(44.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isSelected) SummerTheme.colors.secondaryAccent else SummerTheme.colors.textBrown
            )
        }
    }
}
