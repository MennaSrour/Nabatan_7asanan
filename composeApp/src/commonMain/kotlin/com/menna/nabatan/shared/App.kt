package com.menna.nabatan.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Foundation UI for the new iOS host; existing Android screens remain in :app. */
@Composable
fun App() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                Text("Nabatan — Multiplatform foundation")
            }
        }
    }
}
