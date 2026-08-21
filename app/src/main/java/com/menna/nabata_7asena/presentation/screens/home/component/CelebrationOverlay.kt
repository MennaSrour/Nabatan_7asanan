package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun CelebrationOverlay(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SummerTheme.colors.overlayDark.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoubleLottieCelebration(
                    spec1 = LottieCompositionSpec.RawRes(R.raw.win_celebration),
                    spec2 = LottieCompositionSpec.RawRes(R.raw.big_star)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ما شاء الله عليك",
                    color = SummerTheme.colors.white,
                    style = SummerTheme.typography.overlayTitle,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "حصلت على نجمة البطل الذهبية",
                    color = SummerTheme.colors.goldWarm,
                    style = SummerTheme.typography.overlaySubtitle
                )
            }
        }
    }
}