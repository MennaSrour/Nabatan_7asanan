package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.domain.entity.AyahModel

sealed class QuranAudioState {
    data object Idle : QuranAudioState()

    data class Loading(val suraNo: Int, val ayaNo: Int) : QuranAudioState()
    data class Playing(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    data class Paused(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    data class Error(val message: String) : QuranAudioState()
}

data class SurahDownloadState(
    val downloadedAyahs: Int = 0,
    val totalAyahs: Int = 0,
    val isDownloading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

data class FullQuranDownloadState(
    val isDownloading: Boolean = false,
    val completedSurahs: Int = 0,
    val totalSurahs: Int = 114,
    val currentSurahName: String = "",
    val isComplete: Boolean = false,
    val error: String? = null
)

data class QuranUiState(
    val savedPage: Int = 1,
    val audioState: QuranAudioState = QuranAudioState.Idle,
    val downloadStates: Map<Int, SurahDownloadState> = emptyMap(),
    val fullDownloadState: FullQuranDownloadState = FullQuranDownloadState(),
    val bookmarkedSurahId: Int = -1,
    val bookmarkedAyahNo: Int = -1,
    val ayahsBySurah: Map<Int, List<AyahModel>> = emptyMap(),
    val activeJuz: Int = 1,
    val currentPageNumber: Int = 1,
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
