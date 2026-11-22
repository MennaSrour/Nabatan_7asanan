package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.data.local.PrayerSettingsStore
import androidx.lifecycle.repeatOnLifecycle
import com.menna.nabatan_7asena.core.util.DateUtils
import com.menna.nabatan_7asena.core.util.SeasonalThemeManager
import com.menna.nabatan_7asena.presentation.screens.seasonal.RAMADAN_CALENDAR_ROUTE
import com.menna.nabatan_7asena.presentation.screens.seasonal.RamadanCalendarScreen
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import com.menna.nabatan_7asena.platform.platformKidsRewardsViewModel
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
import com.menna.nabatan_7asena.platform.platformHomeViewModel
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesScreen
import com.menna.nabatan_7asena.presentation.screens.prayerguide.PrayerGuideScreen
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
    openSettings: () -> Unit,
    openFriends: () -> Unit = {}
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val prayerSettings = PrayerSettingsStore.state.collectAsStateWithLifecycle()
    val offset = prayerSettings.value.hijriDateOffset
    val hijriDate = produceState(DateUtils.currentHijriParts(offset), lifecycle, offset) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                value = DateUtils.currentHijriParts(offset)
                delay(30_000)
            }
        }
    }
    val rewards = platformKidsRewardsViewModel()
    LaunchedEffect(rewards) { rewards.syncWhileOpen() }
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                seasonalMode = SeasonalThemeManager.mode(hijriDate.value),
                onNavigateToRamadanCalendar = {
                    navController.navigate(RAMADAN_CALENDAR_ROUTE) { launchSingleTop = true }
                },
                onNavigateToSettings = openSettings,
                onNavigateToFriends = openFriends,
                onNavigateToWheel = { navController.navigate(WheelRoute) { launchSingleTop = true } },
                onNavigateToPrayerGuide = { navController.navigate("prayer-guide") { launchSingleTop = true } },
                onNavigateToBadges = { navController.navigate("progress") { launchSingleTop = true } },
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
        composable(RAMADAN_CALENDAR_ROUTE) { entry ->
            val home = remember(entry) { navController.getBackStackEntry("home") }
            CompositionLocalProvider(LocalViewModelStoreOwner provides home) {
                RamadanCalendarScreen(
                    SeasonalThemeManager.ramadan(hijriDate.value),
                    platformHomeViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
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
        composable("quran/khatmah") { QuranScreen(openKhatmah = true) }
        composable("quran/wird/{page}", arguments = listOf(navArgument("page") { type = NavType.IntType })) { entry ->
            QuranScreen(initialWirdPage = entry.arguments?.read { getInt("page") })
        }
    }
}
