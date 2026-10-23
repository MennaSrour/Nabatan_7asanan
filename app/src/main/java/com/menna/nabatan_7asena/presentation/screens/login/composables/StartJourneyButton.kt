package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun StartJourneyButton(isLoading: Boolean, onClick: () -> Unit) {
    val buttonPulse = rememberGentlePulse()

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = ShapeSmall,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                brush = Brush.horizontalGradient(
                    listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep)
                ),
                shape = ShapeSmall
            ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = SummerTheme.colors.white,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    genderedStringResource(GenderedStrings.startJourney),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.overlayDark,
                    modifier = Modifier.scale(buttonPulse)
                )
            }
        }
    }
}
