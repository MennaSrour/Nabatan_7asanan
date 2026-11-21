package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun FloatingBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isDark = isSystemInDarkTheme()
    val navAccent = SummerTheme.colors.goldWarm

    val items = listOf(
        BottomNavItem("home", Icons.Rounded.Home, navAccent),
        BottomNavItem("quran", Icons.AutoMirrored.Rounded.MenuBook, navAccent),
        BottomNavItem("sebha", Icons.Rounded.Spa, navAccent),
        BottomNavItem("dua", Icons.Rounded.NightsStay, navAccent),
        BottomNavItem("leaderboard", Icons.Rounded.EmojiEvents, navAccent),
        BottomNavItem("quiz", Icons.Rounded.WorkspacePremium, navAccent)
    )

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .height(58.dp)
            .fillMaxWidth()
            .shadow(
            elevation = if (isDark) 0.dp else 8.dp,
            shape = RoundedCornerShape(26.dp),
            spotColor = navAccent.copy(alpha = 0.18f)
        ),
        shape = RoundedCornerShape(26.dp),
        color = SummerTheme.colors.cardBackground.copy(alpha = if (isDark) 0.97f else 0.92f),
        border = BorderStroke(
            1.dp,
            if (isDark) SummerTheme.colors.white.copy(alpha = 0.08f)
            else SummerTheme.colors.white.copy(alpha = 0.55f)
        ),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
