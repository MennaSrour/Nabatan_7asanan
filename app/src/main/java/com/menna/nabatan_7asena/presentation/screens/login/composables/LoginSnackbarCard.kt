package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginSnackbarCard(message: String) {
    Card(
        shape = ShapeMedium,
        colors = CardDefaults.cardColors(
            containerColor = SummerTheme.colors.cardBackground
        ),
        border = BorderStroke(2.dp, SummerTheme.colors.goldWarm),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = SummerTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
