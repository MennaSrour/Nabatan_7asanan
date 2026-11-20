package com.menna.nabatan.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.menna.nabatan_7asena.data.remote.FirebaseAvailability

fun MainViewController(firebaseConfigured: Boolean): platform.UIKit.UIViewController {
    FirebaseAvailability.isConfigured = firebaseConfigured
    return ComposeUIViewController { App() }
}
