package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingPage
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "image_float")
    val translateY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y_axis"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val imageSize = (maxHeight * 0.42f).coerceIn(160.dp, 300.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(page.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .graphicsLayer { translationY = translateY.dp.toPx() }
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = genderedStringResource(page.title),
                style = SummerTheme.typography.overlayTitle,
                color = SummerTheme.colors.textDarkBrown,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.widthIn(max = 480.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = genderedStringResource(page.description),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = SummerTheme.colors.textBrown,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .padding(horizontal = 16.dp)
            )

            AnimatedVisibility(
                visible = pageIndex >= 3,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.widthIn(max = 480.dp)) {
                    Spacer(Modifier.height(24.dp))

                    PermissionInfoCard(
                        icon = when (pageIndex) {
                            3 -> Icons.Default.LocationOn
                            4 -> Icons.Default.Notifications
                            5 -> Icons.Default.BatteryChargingFull
                            else -> Icons.Default.LocationOn
                        },
                        text = when (pageIndex) {
                            3 -> genderedStringResource(GenderedStrings.onboardingLocationHint)
                            4 -> genderedStringResource(GenderedStrings.onboardingNotificationHint)
                            5 -> genderedStringResource(GenderedStrings.onboardingBatteryHint)
                            else -> ""
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
