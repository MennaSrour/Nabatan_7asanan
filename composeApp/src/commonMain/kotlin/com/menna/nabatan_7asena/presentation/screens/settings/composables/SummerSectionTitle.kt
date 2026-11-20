package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerSectionTitle(text: String) {
    Card(
        shape = SummerTheme.Shapes.SmallRounded,
        colors = CardDefaults.cardColors(
            containerColor = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.15f)
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SummerTheme.colors.goldDeep,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
