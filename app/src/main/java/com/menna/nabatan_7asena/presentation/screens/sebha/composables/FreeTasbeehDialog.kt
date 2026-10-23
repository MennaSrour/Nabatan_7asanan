package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun FreeTasbeehDialog(
    onDismiss: () -> Unit,
    onConfirm: (text: String, target: Int?) -> Unit
) {
    var zikrText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SummerTheme.colors.cardBackground,
        title = {
            Text(
                text = genderedStringResource(GenderedStrings.sebhaFree),
                fontWeight = FontWeight.Bold,
                color = SummerTheme.colors.textPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = genderedStringResource(GenderedStrings.sebhaCustomDescription),
                    fontSize = 13.sp,
                    color = SummerTheme.colors.textPrimary.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = zikrText,
                    onValueChange = { zikrText = it },
                    label = { Text(genderedStringResource(GenderedStrings.sebhaOptionalText)) },
                    placeholder = { Text(genderedStringResource(GenderedStrings.sebhaTextExample)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SummerTheme.Colors.PrimaryGold,
                        cursorColor = SummerTheme.Colors.PrimaryGold
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { input -> if (input.all { it.isDigit() }) targetText = input },
                    label = { Text(genderedStringResource(GenderedStrings.sebhaOptionalCount)) },
                    placeholder = { Text(genderedStringResource(GenderedStrings.sebhaUnlimitedHint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SummerTheme.Colors.PrimaryGold,
                        cursorColor = SummerTheme.Colors.PrimaryGold
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                    onConfirm(zikrText.trim(), targetText.trim().toIntOrNull())
            }) {
                Text(genderedStringResource(GenderedStrings.start), color = SummerTheme.Colors.PrimaryGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(genderedStringResource(GenderedStrings.cancel), color = SummerTheme.colors.textPrimary.copy(alpha = 0.7f))
            }
        }
    )
}
