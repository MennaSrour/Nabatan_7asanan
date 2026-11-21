package com.menna.nabatan_7asena.presentation.composables

import com.menna.nabatan_7asena.presentation.screens.shorts.KIDS_SHORTS_ROUTE
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.menna.nabatan_7asena.presentation.screens.update.UpdateDialog

@Composable
fun MainContent(navController: NavHostController, openSettings: () -> Unit) {
    val entry by navController.currentBackStackEntryAsState()
    Box(Modifier.fillMaxSize()) {
        NavigationGraph(navController, openSettings)
        when (entry?.destination?.route) {
            "garden", KIDS_SHORTS_ROUTE -> Unit
            else -> FloatingBottomBar(navController, Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp))
        }
        UpdateDialog()
    }
}
