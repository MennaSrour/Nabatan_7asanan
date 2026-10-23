package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
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
        composable("sebha") { SebhaScreen() }
        composable("leaderboard") { LeaderboardScreen() }
        composable("quiz") { QuizScreen() }
        composable("dua") { AzkarAndDuaScreen() }
        composable("quran") { QuranScreen() }
    }
}
