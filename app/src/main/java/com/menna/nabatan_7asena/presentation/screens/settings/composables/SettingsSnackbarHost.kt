package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        Card(
            shape = SummerTheme.Shapes.MediumRounded,
            colors = CardDefaults.cardColors(
                containerColor = SummerTheme.Colors.BackgroundSunny
            ),
            border = BorderStroke(2.dp, SummerTheme.Colors.PrimaryGold)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("☀️", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = data.visuals.message,
                    color = SummerTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
