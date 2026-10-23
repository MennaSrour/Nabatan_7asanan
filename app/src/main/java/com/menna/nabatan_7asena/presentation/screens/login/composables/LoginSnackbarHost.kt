package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState) { data ->
        LoginSnackbarCard(data.visuals.message)
    }
}
