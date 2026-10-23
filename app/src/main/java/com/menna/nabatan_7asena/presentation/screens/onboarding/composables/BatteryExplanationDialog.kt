package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingActions
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun BatteryExplanationDialog(actions: OnboardingActions) {
    AlertDialog(
        onDismissRequest = { actions.dismissBatteryExplanation() },
        containerColor = SummerTheme.colors.cardBackground,
        title = {
            Text(
                text = genderedStringResource(GenderedStrings.onboardingBatteryTitle),
                color = SummerTheme.colors.textDarkBrown,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = genderedStringResource(GenderedStrings.onboardingBatteryExplanation),
                color = SummerTheme.colors.textBrown
            )
        },
        confirmButton = {
            TextButton(onClick = {
                    actions.confirmBatteryExplanation()
            }) {
                Text(
                    text = genderedStringResource(GenderedStrings.onboardingContinue),
                    color = SummerTheme.colors.wisdomAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { actions.dismissBatteryExplanation() }) {
                Text(genderedStringResource(GenderedStrings.later), color = SummerTheme.colors.textBrown)
            }
        }
    )
}
