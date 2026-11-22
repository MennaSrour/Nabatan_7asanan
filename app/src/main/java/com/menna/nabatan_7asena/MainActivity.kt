package com.menna.nabatan_7asena

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.menna.nabatan.shared.App
import com.menna.nabatan.shared.AppNavigation
import androidx.activity.result.contract.ActivityResultContracts
import com.menna.nabatan_7asena.platform.BroadcastNotificationManager
import com.menna.nabatan_7asena.ui.theme.isNightFallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        var waiting = true
        splash.setKeepOnScreenCondition { waiting }
        lifecycleScope.launch { delay(1000); waiting = false }
        val style = if (isNightFallback()) SystemBarStyle.dark(Color.TRANSPARENT)
            else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        handleIntent(intent)
        setContent { App() }
        BroadcastNotificationManager.requestPermissionForReturningUser(notificationPermission::launch)
    }
    override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); handleIntent(intent) }
    private fun handleIntent(intent: Intent?) {
        AppNavigation.destination.value = intent?.getStringExtra("DESTINATION_ROUTE") ?: intent?.getStringExtra("DESTINATION")
    }
}
