package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.menna.nabatan_7asena.platform.platformKidsRewardsViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.menna.nabatan_7asena.presentation.screens.dua.AzkarAndDuaScreen
import com.menna.nabatan_7asena.presentation.screens.home.HomeScreen
import com.menna.nabatan_7asena.presentation.screens.leaderboard.LeaderboardScreen
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizScreen
import com.menna.nabatan_7asena.presentation.screens.quran.QuranScreen
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaScreen
import com.menna.nabatan_7asena.presentation.screens.rewards.DailyWheelScreen

internal const val WheelRoute = "daily-wheel"

@Composable
fun NavigationGraph(
    navController: NavHostController,
    openSettings: () -> Unit
) {
    val rewards = platformKidsRewardsViewModel()
    LaunchedEffect(rewards) { rewards.syncWhileOpen() }
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = openSettings,
                onNavigateToWheel = { navController.navigate(WheelRoute) { launchSingleTop = true } },
                onNavigateToGarden = { navController.navigate("garden") { launchSingleTop = true } },
                onNavigateToQuran = {
                    navController.navigate("quran") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSebha = {
                    navController.navigate("sebha") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("garden") { entry ->
            val home = remember(entry) { navController.getBackStackEntry("home") }
            CompositionLocalProvider(LocalViewModelStoreOwner provides home) {
                com.menna.nabatan_7asena.presentation.screens.home.GardenScreen(
                    com.menna.nabatan_7asena.platform.platformHomeViewModel(), onBack = { navController.popBackStack() })
            }
        }
        composable(WheelRoute) { entry ->
            val home = remember(entry) { navController.getBackStackEntry("home") }
            CompositionLocalProvider(LocalViewModelStoreOwner provides home) {
                DailyWheelScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = rewards,
                    home = com.menna.nabatan_7asena.platform.platformHomeViewModel()
                )
            }
        }
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("quiz") { QuizScreen() }
        composable("dua") { AzkarAndDuaScreen() }
        composable("quran") { QuranScreen() }
    }
}
