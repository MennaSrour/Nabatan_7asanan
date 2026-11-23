package com.menna.nabatan.shared

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.menna.nabatan_7asena.platform.platformMainViewModel
import com.menna.nabatan_7asena.platform.platformFriendsViewModel
import com.menna.nabatan_7asena.presentation.screens.friends.FriendsScreen
import com.menna.nabatan_7asena.presentation.screens.friends.LocalFriendsViewModel
import com.menna.nabatan_7asena.presentation.screens.friends.PresetChatScreen
import com.menna.nabatan_7asena.presentation.MainScreenHolder
import com.menna.nabatan_7asena.presentation.common.LocalUserGender
import com.menna.nabatan_7asena.presentation.screens.start.StartScreen
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingScreen
import com.menna.nabatan_7asena.presentation.screens.login.LoginScreen
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsScreen
import com.menna.nabatan_7asena.presentation.screens.quran.QuranScreen
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaScreen
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import com.menna.nabatan_7asena.ui.theme.isNightFallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

object AppNavigation { val destination = MutableStateFlow<String?>(null) }

@Composable
fun App() {
    val model = platformMainViewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    val nav = rememberNavController()
    LaunchedEffect(model) { model.enableDailyAutoScheduling() }
    LaunchedEffect(nav) {
        AppNavigation.destination.collectLatest { route ->
            if (route == "quran" || route == "sebha") {
                nav.currentBackStackEntryFlow.first {
                    it.destination.route in listOf("home", "settings", "quran", "sebha")
                }
                nav.navigate(route) { launchSingleTop = true }
                AppNavigation.destination.value = null
            }
        }
    }
    CompositionLocalProvider(LocalUserGender provides state.gender, LocalFriendsViewModel provides platformFriendsViewModel()) {
        NabataAppTheme(isNightFallback()) { SharedNavigation(nav, model::startManagerWorkerImmediately) }
    }
}

@Composable
private fun SharedNavigation(nav: androidx.navigation.NavHostController, setup: () -> Unit) {
    NavHost(nav, startDestination = "splash") {
        composable("splash") {
            StartScreen(onNavigateToHome = { nav.navigate("home") { popUpTo("splash") { inclusive = true } } },
                onNavigateToOnboarding = { nav.navigate("onboarding") { popUpTo("splash") { inclusive = true } } },
                onNavigateToLogin = { nav.navigate("login") { popUpTo("splash") { inclusive = true } } })
        }
        composable("onboarding") { OnboardingScreen(onNavigateToLogin = { setup(); nav.navigate("login") { popUpTo("onboarding") { inclusive = true } } }) }
        composable("login") { LoginScreen({ nav.navigate("home") { popUpTo("login") { inclusive = true } } }) }
        composable("settings") { SettingsScreen({ nav.popBackStack() }, { nav.navigate("splash") { popUpTo(0) { inclusive = true } } }) }
        composable("home") {
            MainScreenHolder(openFriends = { nav.navigate("friends") { launchSingleTop = true } }) {
                nav.navigate("settings")
            }
        }
        composable("friends") {
            FriendsScreen(onBack = { nav.popBackStack() }) { chatId ->
                nav.navigate("friends/chat/$chatId") { launchSingleTop = true }
            }
        }
        composable("friends/chat/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { entry ->
            entry.arguments?.read { getString("chatId") }?.let { chatId ->
                PresetChatScreen(chatId) { nav.popBackStack() }
            }
        }
        composable("quran") { QuranScreen() }
        composable("sebha") { SebhaScreen() }
    }
}
