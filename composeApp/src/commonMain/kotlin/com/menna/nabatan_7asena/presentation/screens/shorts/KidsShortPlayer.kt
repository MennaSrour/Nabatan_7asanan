package com.menna.nabatan_7asena.presentation.screens.shorts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal enum class VideoState { Loading, Ready, Failed }

@Composable
internal expect fun KidsShortPlayer(
    uri: String,
    playing: Boolean,
    muted: Boolean,
    onState: (VideoState) -> Unit,
    modifier: Modifier
)
