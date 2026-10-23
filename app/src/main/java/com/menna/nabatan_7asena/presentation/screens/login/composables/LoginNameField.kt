package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDirection
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.login.LoginActions
import com.menna.nabatan_7asena.presentation.screens.login.LoginUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginNameField(state: LoginUiState, actions: LoginActions) {
    OutlinedTextField(
        value = state.name,
        onValueChange = { actions.onNameChange(it) },
        label = { Text(genderedStringResource(GenderedStrings.loginNameHint)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(
            textDirection = TextDirection.ContentOrRtl
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = SummerTheme.colors.cardBackground,
            unfocusedContainerColor = SummerTheme.colors.cardBackground,
            focusedTextColor = SummerTheme.colors.textPrimary,
            unfocusedTextColor = SummerTheme.colors.textPrimary,
            cursorColor = SummerTheme.colors.wisdomAccent,
            focusedLabelColor = SummerTheme.colors.wisdomAccent,
            unfocusedLabelColor = SummerTheme.colors.textBrown
        ),
        shape = ShapeMedium
    )
}
