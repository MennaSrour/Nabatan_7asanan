package com.menna.nabatan_7asena.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.menna.nabatan_7asena.presentation.composables.MainContent

@Composable
fun MainScreenHolder(openFriends: () -> Unit = {}, openSettings: () -> Unit) {
    MainContent(rememberNavController(), openSettings, openFriends)
}
