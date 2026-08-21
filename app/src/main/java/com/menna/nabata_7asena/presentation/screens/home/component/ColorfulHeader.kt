package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import com.menna.nabata_7asena.ui.theme.SummerTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val NightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFD8DCF6),
        Color(0xFFA0AADA),
        Color(0xFF3A5AD0)
    )
)

private object HeaderWaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.85f)
            cubicTo(
                size.width * 0.72f, size.height * 0.94f,
                size.width * 0.32f, size.height * 0.58f,
                0f, size.height * 0.95f
            )
            lineTo(0f, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun ClayAssetImage(
    resName: String,
    fallbackIcon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val context = LocalContext.current
    val resId = remember(resName) {
        context.resources.getIdentifier(resName, "drawable", context.packageName)
    }
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = modifier
                .background(tint.copy(alpha = 0.12f), CircleShape)
                .border(1.5.dp, tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}

@Composable
fun ColorfulHeader(
    user: HomeUiState.UiUser?,
    hijriDate: String,
    onSettingsClick: () -> Unit,
    onAvatarClick: () -> Unit,
    isNight: Boolean = false
) {
    val headerTextColor = if (isNight) SummerTheme.colors.white else SummerTheme.colors.headerTextDark
    val settingsIconTint = if (isNight) SummerTheme.colors.white else SummerTheme.colors.settingsIconDark

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val floatingOffset = SummerTheme.rememberFloatingAnimation()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clipToBounds()
                .clip(HeaderWaveShape)
        ) {
            Image(
                painter = painterResource(if (isNight) R.drawable.header_night else R.drawable.header_day),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .offset(y = floatingOffset.dp)
                            .clickable { onAvatarClick() }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SummerTheme.colors.white.copy(alpha = 0.15f),
                            border = BorderStroke(
                                3.dp,
                                Brush.linearGradient(listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep))
                            ),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(66.dp)
                                .align(Alignment.Center)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.welcome),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "أهلاً يا ${user?.name ?: "بطلنا"}",
                            style = SummerTheme.typography.headerWelcome,
                            color = headerTextColor
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SummerTheme.colors.white.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, SummerTheme.colors.white.copy(alpha = 0.6f)),
                            shadowElevation = 4.dp,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = hijriDate,
                                    style = SummerTheme.typography.headerDate,
                                    color = headerTextColor
                                )
                            }
                        }
                    }
                }
                Surface(
                    onClick = onSettingsClick,
                    shape = CircleShape,
                    color = SummerTheme.colors.white.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SummerTheme.colors.white),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = settingsIconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}