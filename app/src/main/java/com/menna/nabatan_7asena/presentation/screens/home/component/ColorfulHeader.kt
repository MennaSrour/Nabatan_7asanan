package com.menna.nabatan_7asena.presentation.screens.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedString
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

val NightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2E2A5C),
        Color(0xFF4B3F86),
        Color(0xFF7A5FD1)
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
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.18f), Color.Transparent),
                        endY = 220f
                    )
                )
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
                        text = genderedStringResource(GenderedStrings.homeWelcome, user?.name ?: genderedStringResource(GenderedStrings.homeDefaultName)),
                        style = SummerTheme.typography.headerWelcome,
                        color = headerTextColor
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(top=18.dp),
                        color = SummerTheme.colors.white.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, SummerTheme.colors.white.copy(alpha = 0.6f)),
                        shadowElevation = 4.dp,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.calendar),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
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
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                       contentDescription = genderedStringResource(GenderedStrings.settings),
                        tint = settingsIconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}