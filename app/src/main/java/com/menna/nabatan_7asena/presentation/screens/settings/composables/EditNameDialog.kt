package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditNameDialog(actions: SettingsActions, onDismiss: () -> Unit) {
    var tempName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = SummerTheme.Colors.BackgroundSunny,
        shape = SummerTheme.Shapes.ExtraRounded,
        icon = { Text("✏️", fontSize = 48.sp) },
        title = {
            Text(
                genderedStringResource(GenderedStrings.settingsNewName),
                color = SummerTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text(genderedStringResource(GenderedStrings.settingsNameLabel)) },
                shape = SummerTheme.Shapes.MediumRounded,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SummerTheme.Colors.PrimaryGold,
                    unfocusedBorderColor = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.5f)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tempName.isNotBlank()) {
                        actions.updateUserName(tempName)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SummerTheme.Colors.PrimaryGold
                ),
                shape = SummerTheme.Shapes.SmallRounded
            ) {
                Text(genderedStringResource(GenderedStrings.save), fontWeight = FontWeight.Bold, color = SummerTheme.colors.white)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(genderedStringResource(GenderedStrings.cancel), color = SummerTheme.colors.textBrown, fontWeight = FontWeight.Bold)
            }
        }
    )
}
