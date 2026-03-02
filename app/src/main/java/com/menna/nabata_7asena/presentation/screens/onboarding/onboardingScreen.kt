package com.menna.nabata_7asena.presentation.screens.onboarding

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentPage by viewModel.currentPageIndex
    val pages = viewModel.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val batteryOptimizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {

    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePageIndex(pagerState.currentPage)
    }

    LaunchedEffect(
        locationPermission.status.isGranted,
        notificationPermission?.status?.isGranted
    ) {
        when (currentPage) {
            3 -> {
                if (locationPermission.status.isGranted) {
                    pagerState.animateScrollToPage(4)
                }
            }
            4 -> {
                if (notificationPermission == null || notificationPermission.status.isGranted) {
                    pagerState.animateScrollToPage(5)
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1565C0),
                                Color(0xFF42A5F5)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { index ->
                        PageIndicator(
                            isSelected = index == currentPage,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { index ->
                    OnboardingPageContent(
                        page = pages[index],
                        pageIndex = index
                    )
                }

                BottomSection(
                    currentPage = currentPage,
                    totalPages = pages.size,
                    onNextClicked = {
                        scope.launch {
                            when (currentPage) {
                                3 -> {
                                    if (locationPermission.status.isGranted) {
                                        pagerState.animateScrollToPage(4)
                                    } else {
                                        locationPermission.launchPermissionRequest()
                                    }
                                }
                                4 -> {
                                    if (notificationPermission == null || notificationPermission.status.isGranted) {
                                        pagerState.animateScrollToPage(5)
                                    } else {
                                        notificationPermission.launchPermissionRequest()
                                    }
                                }
                                5 -> {
                                    if (viewModel.isBatteryOptimizationDisabled(context)) {
                                        viewModel.saveOnboardingCompleted()
                                        onNavigateToLogin()
                                    } else {
                                        val intent = Intent().apply {
                                            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                            data = Uri.parse("package:${context.packageName}")
                                        }
                                        batteryOptimizationLauncher.launch(intent)
                                    }
                                }
                                else -> {
                                    if (currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(currentPage + 1)
                                    }
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
fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "width"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(8.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(320.dp)
                .graphicsLayer {
                    translationY = translateY.dp.toPx()
                }
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = page.title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1565C0),
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        AnimatedVisibility(
            visible = pageIndex >= 3,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Spacer(Modifier.height(24.dp))

            PermissionInfoCard(
                icon = when (pageIndex) {
                    3 -> Icons.Default.LocationOn
                    4 -> Icons.Default.Notifications
                    5 -> Icons.Default.BatteryChargingFull
                    else -> Icons.Default.LocationOn
                },
                text = when (pageIndex) {
                    3 -> "ساعد تيمور يعرف القبلة ومواقيت الصلاة في مدينتك 🌍"
                    4 -> "مش عاوزين يفوتنا أذان ولا ذكر، اسمح لتيمور ينبهك ✨"
                    5 -> "عشان تيمور يفضل صاحي وينبهك في الوقت الصح 🔋"
                    else -> ""
                }
            )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
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
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF1565C0),
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
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
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSkipClicked,
                enabled = currentPage < totalPages - 1
            ) {
                Text(
                    text = if (currentPage >= 3) "تخطي الكل" else "تخطي",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onNextClicked,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = when (currentPage) {
                        totalPages - 1 -> "ابدأ الرحلة 🚀"
                        3 -> "السماح بالموقع"
                        4 -> "السماح بالإشعارات"
                        5 -> "تحسين البطارية"
                        else -> "التالي"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)