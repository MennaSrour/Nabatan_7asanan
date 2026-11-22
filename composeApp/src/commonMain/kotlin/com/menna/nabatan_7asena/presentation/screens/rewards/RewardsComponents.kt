package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.KidsFontFamily

internal val RewardInk = Color(0xFF382452)
internal val RewardPurple = Color(0xFF664495)
internal val RewardCream = Color(0xFFFFFAEC)
internal val RewardLavender = Color(0xFFEDE1FA)
internal val RewardGold = Color(0xFFFFCE6D)
internal val RewardMint = Color(0xFFD6EFDC)
internal val RewardRose = Color(0xFFF6DCE4)
internal val RewardShape = RoundedCornerShape(22.dp)

internal fun arabicNumber(value: Int) = value.toString().map {
    if (it in '0'..'9') '٠' + (it - '0') else it
}.joinToString("")

@Composable
internal fun RewardText(text: String, modifier: Modifier = Modifier, size: Int = 19, bold: Boolean = false) {
    Text(text, modifier, color = RewardInk, fontFamily = KidsFontFamily, fontSize = size.sp,
        lineHeight = (size + 7).sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
}

@Composable
internal fun RewardButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = RewardLavender
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 50.dp),
        enabled = enabled,
        shape = RewardShape,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = RewardInk)
    ) {
        Text(label, fontFamily = KidsFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
    }
}

@Composable
internal fun RewardsPage(
    title: String,
    onBack: () -> Unit,
    scroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(RewardLavender, RewardCream)))
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.widthIn(max = 640.dp).fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = RewardInk)
                }
                RewardText(title, size = 27, bold = true)
            }
            val body = Modifier.widthIn(max = 580.dp).fillMaxWidth().weight(1f)
            Column(
                (if (scroll) body.verticalScroll(rememberScrollState()) else body)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

@Composable
internal fun RoundProgress(value: Float) {
    LinearProgressIndicator(
        progress = { value.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth().height(9.dp),
        color = RewardPurple,
        trackColor = RewardLavender
    )
}
