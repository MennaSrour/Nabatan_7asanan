package com.menna.nabatan_7asena.presentation.screens.friends

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

@Composable
internal actual fun rememberFriendCodeCopier(): (String) -> Unit =
    { code -> UIPasteboard.generalPasteboard.string = code }
