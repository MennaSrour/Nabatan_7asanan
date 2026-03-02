package com.menna.nabata_7asena.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.menna.nabata_7asena.presentation.screens.badges.BadgesScreen
import com.menna.nabata_7asena.presentation.screens.home.HomeScreen
import com.menna.nabata_7asena.presentation.screens.leaderboard.LeaderboardScreen
import com.menna.nabata_7asena.presentation.screens.quran.QuranScreen
import com.menna.nabata_7asena.presentation.screens.sebha.SebhaScreen
import com.menna.nabata_7asena.presentation.screens.update.DownloadState
import com.menna.nabata_7asena.presentation.screens.update.UpdateDialog
import com.menna.nabata_7asena.presentation.screens.update.UpdateUiState
import com.menna.nabata_7asena.presentation.screens.update.UpdateViewModel

@Composable
fun MainScreenHolder(
    openSettings: () -> Unit,
    updateViewModel: UpdateViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val updateUiState by updateViewModel.uiState.collectAsState()
    val downloadState by updateViewModel.downloadState.collectAsState()

    LaunchedEffect(Unit) {
        updateViewModel.checkForUpdates()
    }

    LaunchedEffect(downloadState) {
        if (downloadState is DownloadState.Completed) {
            updateViewModel.dismissUpdate()
            updateViewModel.resetDownloadState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavigationGraph(
            navController = navController,
            openSettings = openSettings
        )

        FloatingBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        when (val state = updateUiState) {
            is UpdateUiState.UpdateAvailable -> {
                UpdateDialog(
                    info = state.info,
                    downloadState = downloadState,
                    onUpdateClick = {
                        if (downloadState is DownloadState.Error) {
                            updateViewModel.resetDownloadState()
                        }
                        updateViewModel.startDownload(state.info.downloadUrl)
                    },
                    onDismiss = {
                        updateViewModel.dismissUpdate()
                        updateViewModel.resetDownloadState()
                    }
                )
            }

            else -> {}
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    openSettings: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(onNavigateToSettings = openSettings) }
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("badges") { BadgesScreen() }
        composable("quran") { QuranScreen() }
    }
}

@Composable
fun FloatingBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem("home", Icons.Rounded.Home, Color(0xFF42A5F5)),
        BottomNavItem("quran", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFF4CAF50)),
        BottomNavItem("sebha", Icons.Rounded.Spa, Color(0xFF66BB6A)),
        BottomNavItem("leaderboard", Icons.Rounded.EmojiEvents, Color(0xFFFFCA28)),
        BottomNavItem("badges", Icons.Rounded.WorkspacePremium, Color(0xFFAB47BC))
    )

    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .height(70.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(35.dp),
                spotColor = Color(0xFF90A4AE).copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(35.dp),
        color = Color.White.copy(alpha = 0.95f),
        tonalElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                FloatingNavItem(
                    item = item,
                    isSelected = currentRoute == item.route
                ) {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) item.color.copy(alpha = 0.15f) else Color.Transparent,
        label = "bgColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) item.color else Color.Gray.copy(alpha = 0.5f),
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .size(50.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val color: Color
)