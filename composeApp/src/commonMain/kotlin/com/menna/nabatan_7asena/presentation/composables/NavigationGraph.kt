package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
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
import com.menna.nabatan_7asena.domain.entity.KidsGame
import com.menna.nabatan_7asena.presentation.screens.rewards.DailyWheelScreen
import com.menna.nabatan_7asena.presentation.screens.rewards.KidsGamesScreen
import com.menna.nabatan_7asena.presentation.screens.rewards.KidsGameScreen

internal const val WheelRoute = "daily-wheel"
internal const val GamesRoute = "kids-games"
private fun gameRoute(game: KidsGame) = "$GamesRoute/${game.name.lowercase()}"

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
                onNavigateToWheel = { navController.navigate(WheelRoute) { launchSingleTop = true } },
                onNavigateToGames = { navController.navigate(GamesRoute) { launchSingleTop = true } },
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
        composable(WheelRoute) {
            DailyWheelScreen(
                onBack = { navController.popBackStack() },
                onGames = { navController.navigate(GamesRoute) { launchSingleTop = true } },
                onBonus = { navController.navigate(gameRoute(KidsGame.BONUS)) { launchSingleTop = true } }
            )
        }
        composable(GamesRoute) {
            KidsGamesScreen(
                onBack = { navController.popBackStack() },
                onGame = { game -> navController.navigate(gameRoute(game)) { launchSingleTop = true } }
            )
        }
        KidsGame.entries.forEach { game ->
            composable(gameRoute(game)) { KidsGameScreen(game, onBack = { navController.popBackStack() }) }
        }
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("quiz") { QuizScreen() }
        composable("dua") { AzkarAndDuaScreen() }
        composable("quran") { QuranScreen() }
    }
}
