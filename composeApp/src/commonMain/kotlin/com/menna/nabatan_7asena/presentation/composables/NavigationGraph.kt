package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.menna.nabatan_7asena.platform.platformHomeViewModel
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesScreen
import com.menna.nabatan_7asena.presentation.screens.prayerguide.PrayerGuideScreen
import com.menna.nabatan_7asena.presentation.screens.dua.AzkarAndDuaScreen
import com.menna.nabatan_7asena.presentation.screens.home.HomeScreen
import com.menna.nabatan_7asena.presentation.screens.leaderboard.LeaderboardScreen
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizScreen
import com.menna.nabatan_7asena.presentation.screens.quran.QuranScreen
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    openSettings: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = openSettings,
                onNavigateToPrayerGuide = { navController.navigate("prayer-guide") { launchSingleTop = true } },
                onNavigateToBadges = { navController.navigate("progress") { launchSingleTop = true } },
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
                    platformHomeViewModel(), onBack = { navController.popBackStack() })
            }
        }
        composable("prayer-guide") { entry ->
            val home = remember(entry) { navController.getBackStackEntry("home") }
            CompositionLocalProvider(LocalViewModelStoreOwner provides home) {
                val homeViewModel = platformHomeViewModel()
                PrayerGuideScreen(
                    onBack = { navController.popBackStack() },
                    awardBonus = homeViewModel::awardPrayerGuideBonus
                )
            }
        }
        composable("progress") { entry ->
            val home = remember(entry) { navController.getBackStackEntry("home") }
            CompositionLocalProvider(LocalViewModelStoreOwner provides home) {
                BadgesScreen(onBack = { navController.popBackStack() })
            }
        }
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("quiz") { QuizScreen() }
        composable("dua") { AzkarAndDuaScreen() }
        composable("quran") { QuranScreen() }
    }
}
