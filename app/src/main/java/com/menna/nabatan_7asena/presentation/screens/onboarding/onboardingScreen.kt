package com.menna.nabatan_7asena.presentation.screens.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedString
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OnboardingContent(
            onNavigateToLogin = onNavigateToLogin,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun OnboardingContent(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val context = LocalContext.current
    val currentPage by viewModel.currentPageIndex
    val pages = viewModel.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val isDark = isSystemInDarkTheme()

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val batteryOptimizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {

    }

    var showBatteryExplainDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePageIndex(pagerState.currentPage)
    }

    LaunchedEffect(
        locationPermission.status.isGranted,
        notificationPermission?.status?.isGranted
    ) {
        when (currentPage) {
            3 -> if (locationPermission.status.isGranted) pagerState.animateScrollToPage(4)
            4 -> if (notificationPermission == null || notificationPermission.status.isGranted) {
                pagerState.animateScrollToPage(5)
            }
        }
    }

    if (showBatteryExplainDialog) {
        AlertDialog(
            onDismissRequest = { showBatteryExplainDialog = false },
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
                    showBatteryExplainDialog = false
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:${context.packageName}")
                    }
                    batteryOptimizationLauncher.launch(intent)
                }) {
                    Text(
                        text = genderedStringResource(GenderedStrings.onboardingContinue),
                        color = SummerTheme.colors.wisdomAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatteryExplainDialog = false }) {
                    Text(genderedStringResource(GenderedStrings.later), color = SummerTheme.colors.textBrown)
                }
            }
        )
    }

    Scaffold(containerColor = SummerTheme.colors.dayBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SummerTheme.colors.wisdomStart,
                                    SummerTheme.colors.dayBackground
                                )
                            )
                        )
                )

                OnboardingAtmosphere(
                    isDark = isDark,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SummerTheme.colors.dayBackground.copy(alpha = 0f),
                                    SummerTheme.colors.dayBackground
                                )
                            )
                        )
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { index ->
                        PageIndicator(
                            isSelected = index == currentPage,
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { index ->
                    OnboardingPageContent(page = pages[index], pageIndex = index)
                }

                BottomSection(
                    currentPage = currentPage,
                    totalPages = pages.size,
                    onNextClicked = {
                        scope.launch {
                            when (currentPage) {
                                3 -> if (locationPermission.status.isGranted) {
                                    pagerState.animateScrollToPage(4)
                                } else {
                                    locationPermission.launchPermissionRequest()
                                }

                                4 -> if (notificationPermission == null ||
                                    notificationPermission.status.isGranted
                                ) {
                                    pagerState.animateScrollToPage(5)
                                } else {
                                    notificationPermission.launchPermissionRequest()
                                }

                                5 -> if (viewModel.isBatteryOptimizationDisabled(context)) {
                                    viewModel.saveOnboardingCompleted()
                                    onNavigateToLogin()
                                } else {
                                    showBatteryExplainDialog = true
                                }

                                else -> if (currentPage < pages.size - 1) {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            }
                        }
                    },
                    onSkipClicked = {
                        scope.launch {
                            if (currentPage < 3) {
                                pagerState.animateScrollToPage(3)
                            } else {
                                viewModel.saveOnboardingCompleted()
                                onNavigateToLogin()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OnboardingAtmosphere(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(if (isDark) R.raw.night_stars else R.raw.clouds)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = if (isDark) 0.55f else 0.35f
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer { alpha = if (isDark) 0.85f else 0.55f }
    )
}

@Composable
fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 20.dp else 6.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "indicator_width"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(6.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) SummerTheme.colors.white
                else SummerTheme.colors.white.copy(alpha = 0.45f)
            )
    )
}

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

@Composable
fun PermissionInfoCard(
    icon: ImageVector,
    text: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = SummerTheme.Shapes.MediumRounded,
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.wisdomStart)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SummerTheme.colors.wisdomAccent,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = SummerTheme.colors.wisdomTextPrimary,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )
        }
    }
}

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

data class OnboardingPage(
    val title: GenderedString,
    val description: GenderedString,
    val imageRes: Int
)