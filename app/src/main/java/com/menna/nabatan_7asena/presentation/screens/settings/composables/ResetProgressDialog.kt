package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ResetProgressDialog(actions: SettingsActions, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = SummerTheme.colors.errorRed.copy(alpha = 0.08f),
        shape = SummerTheme.Shapes.ExtraRounded,
        icon = { Text("⚠️", fontSize = 48.sp) },
        title = {
            Text(
                genderedStringResource(GenderedStrings.settingsResetConfirmation),
                color = SummerTheme.colors.errorRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                genderedStringResource(GenderedStrings.settingsResetDescription),
                fontSize = 15.sp,
                color = SummerTheme.colors.wisdomTextSecondary,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    actions.resetProgress()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SummerTheme.colors.errorRed.copy(alpha = 0.2f),
                    contentColor = SummerTheme.colors.errorRed
                ),
                shape = SummerTheme.Shapes.SmallRounded
            ) {
                Text(genderedStringResource(GenderedStrings.settingsResetAction), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(genderedStringResource(GenderedStrings.cancel), color = SummerTheme.colors.textBrown, fontWeight = FontWeight.Bold)
            }
        }
    )
}
