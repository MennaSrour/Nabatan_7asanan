package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.savedstate.read
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                onNavigateToGarden = { navController.navigate("garden") { launchSingleTop = true } },
                onNavigateToKhatmah = { navController.navigate("quran/khatmah") { launchSingleTop = true } },
                onNavigateToQuran = { page ->
                    navController.navigate(if (page == null) "quran" else "quran/wird/$page") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = page == null
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
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("quiz") { QuizScreen() }
        composable("dua") { AzkarAndDuaScreen() }
        composable("quran") { QuranScreen() }
        composable("quran/khatmah") { QuranScreen(openKhatmah = true) }
        composable("quran/wird/{page}", arguments = listOf(navArgument("page") { type = NavType.IntType })) { entry ->
            QuranScreen(initialWirdPage = entry.arguments?.read { getInt("page") })
        }
    }
}
