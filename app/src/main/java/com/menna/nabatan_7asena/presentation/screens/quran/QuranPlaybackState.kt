package com.menna.nabatan_7asena.presentation.screens.quran

internal data class QuranPlaybackState(
    val audioState: QuranAudioState = QuranAudioState.Idle,
    val isRepeatBottomSheetVisible: Boolean = false,
    val repeatSurahId: Int = 1,
    val repeatAyahCount: Int = 0,
    val playbackScope: PlaybackScope = PlaybackScope.FULL_SURAH,
    val startAyah: Int = 1,
    val endAyah: Int = 1,
    val repeatCount: Int = 3,
    val isRepeatModeActive: Boolean = false,
    val remainingRepeats: Int = 0
)
