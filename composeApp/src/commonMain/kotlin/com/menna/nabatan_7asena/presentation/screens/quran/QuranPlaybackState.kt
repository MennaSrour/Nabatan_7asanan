package com.menna.nabatan_7asena.presentation.screens.quran

data class QuranPlaybackState(
    val audioState: QuranAudioState = QuranAudioState.Idle,
    val isRepeatBottomSheetVisible: Boolean = false,
    val repeatSurahId: Int = 1,
    val repeatAyahCount: Int = 0,
    val playbackScope: PlaybackScope = PlaybackScope.FULL_SURAH,
    val startAyah: Int = 1,
    val endAyah: Int = 1,
    val repeatCount: Int = 3,
    val isRepeatModeActive: Boolean = false,
    val playlist: PlaylistPlaybackState? = null,
    val remainingRepeats: Int = 0
)

data class PlaylistTrack(val surahId: Int, val ayahCount: Int)
data class PlaylistPlaybackState(
    val id: String,
    val name: String,
    val tracks: List<PlaylistTrack>,
    val currentIndex: Int = 0,
    val repeat: Boolean = false
)
