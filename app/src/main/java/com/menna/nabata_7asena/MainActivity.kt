package com.menna.nabata_7asena

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.menna.nabata_7asena.core.worker.DailyManagerWorker
import com.menna.nabata_7asena.presentation.MainScreenHolder
import com.menna.nabata_7asena.presentation.MainViewModel
import com.menna.nabata_7asena.presentation.screens.quran.QuranScreen
import com.menna.nabata_7asena.presentation.screens.sebha.SebhaScreen
import com.menna.nabata_7asena.presentation.screens.settings.SettingsScreen
import com.menna.nabata_7asena.presentation.screens.start.StartScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepOnScreen }
        lifecycleScope.launch {
            delay(1000)
            keepOnScreen = false
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        viewModel.enableDailyAutoScheduling()

        setContent {
            val navController = rememberNavController()

            val intent = intent
            LaunchedEffect(intent) {
                checkIntentAndNavigate(intent, navController)
            }

            DisposableEffect(Unit) {
                val listener = androidx.core.util.Consumer<Intent> { newIntent ->
                    checkIntentAndNavigate(newIntent, navController)
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            NavHost(navController = navController, startDestination = "splash") {

                composable("splash") {
                    StartScreen(
                        onNavigateToHome = {
                            navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                        }
                    )
                }

                composable("onboarding") {
                    com.menna.nabata_7asena.presentation.screens.onboarding.OnboardingScreen(
                        onNavigateToLogin = {
                            startManagerWorkerImmediately()

                            navController.navigate("login") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }

                composable("login") {
                    com.menna.nabata_7asena.presentation.screens.login.LoginScreen(
                        onNavigateToHome = {
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate("splash") { popUpTo(0) { inclusive = true } }
                        }
                    )
                }

                composable("home") {
                    MainScreenHolder(
                        openSettings = { navController.navigate("settings") }
                    )
                }

                composable("sebha") { SebhaScreen() }
                composable("quran") { QuranScreen() }
            }
        }
    }

    private fun startManagerWorkerImmediately() {
        try {
            val workRequest = OneTimeWorkRequestBuilder<DailyManagerWorker>()
                .addTag("immediate_setup_work")
                .build()

            WorkManager.getInstance(this).enqueueUniqueWork(
                "immediate_setup_work",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIntentAndNavigate(
        intent: Intent?,
        navController: androidx.navigation.NavController
    ) {
        val destination = intent?.getStringExtra("DESTINATION")
        when (destination) {
            "sebha" -> {
                navController.navigate("sebha") {
                    popUpTo("home") { inclusive = false }
                }
            }
            "quran" -> {
                navController.navigate("quran") {
                    popUpTo("home") { inclusive = false }
                }
            }
        }
    }
}