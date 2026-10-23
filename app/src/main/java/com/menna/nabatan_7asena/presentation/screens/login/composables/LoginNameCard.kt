package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.login.LoginActions
import com.menna.nabatan_7asena.presentation.screens.login.LoginUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun LoginNameCard(state: LoginUiState, actions: LoginActions) {
    Card(
        shape = ShapeMedium,
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.cardBackground),
        elevation = CardDefaults.cardElevation(6.dp),
        border = BorderStroke(
            2.dp,
            if (state.name.isNotBlank()) SummerTheme.colors.goldWarm else SummerTheme.colors.chipUnselected
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        LoginNameField(state, actions)
    }
}
