package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun BottomSection(
    currentPage: Int,
    totalPages: Int,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SummerTheme.colors.cardBackground,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onSkipClicked) {
                Text(
                    text = if (currentPage >= 3) genderedStringResource(GenderedStrings.onboardingSkipAll) else genderedStringResource(GenderedStrings.onboardingSkip),
                    color = SummerTheme.colors.textBrown,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onNextClicked,
                shape = SummerTheme.Shapes.MediumRounded,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SummerTheme.colors.goldWarm
                ),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = when (currentPage) {
                        totalPages - 1 -> genderedStringResource(GenderedStrings.startJourney)
                        3 -> genderedStringResource(GenderedStrings.onboardingAllowLocation)
                        4 -> genderedStringResource(GenderedStrings.onboardingAllowNotifications)
                        5 -> genderedStringResource(GenderedStrings.onboardingBatteryTitle)
                        else -> genderedStringResource(GenderedStrings.next)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.overlayDark
                )
            }
        }
    }
}
