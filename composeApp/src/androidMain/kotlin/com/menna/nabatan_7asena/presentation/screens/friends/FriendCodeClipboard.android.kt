package com.menna.nabatan_7asena.presentation.screens.friends

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberFriendCodeCopier(): (String) -> Unit {
    val clipboard = LocalContext.current.getSystemService(ClipboardManager::class.java)
    return { code -> clipboard.setPrimaryClip(ClipData.newPlainText("كود الصداقة", code)) }
}
