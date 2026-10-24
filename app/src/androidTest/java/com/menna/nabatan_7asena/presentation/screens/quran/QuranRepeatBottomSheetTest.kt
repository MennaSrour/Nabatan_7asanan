package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.menna.nabatan_7asena.domain.Surah
import com.menna.nabatan_7asena.presentation.screens.quran.composables.QuranRepeatBottomSheet
import com.menna.nabatan_7asena.presentation.screens.quran.composables.SurahHeaderCard
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class QuranRepeatBottomSheetTest {
    @get:Rule val compose = createComposeRule()

    @Test fun headerOpensSheetAndCustomSettingsAreSubmitted() {
        val state = mutableStateOf(QuranUiState(repeatAyahCount = 4, endAyah = 4))
        var submitted: QuranUiState? = null
        val actions = object : NoOpQuranActions() {
            override fun onOpenRepeatBottomSheet(surahId: Int) {
                state.value = state.value.copy(repeatSurahId = surahId, isRepeatBottomSheetVisible = true)
            }
            override fun onPlaybackScopeChanged(scope: PlaybackScope) { state.value = state.value.copy(playbackScope = scope) }
            override fun onStartAyahChanged(ayah: Int) { state.value = state.value.copy(startAyah = ayah) }
            override fun onEndAyahChanged(ayah: Int) { state.value = state.value.copy(endAyah = ayah) }
            override fun onRepeatCountChanged(count: Int) { state.value = state.value.copy(repeatCount = count) }
            override fun onStartRepeatClicked() {
                submitted = state.value
                state.value = state.value.copy(isRepeatBottomSheetVisible = false)
            }
        }
        compose.setContent {
            NabataAppTheme(isNight = false) {
                SurahHeaderCard(Surah(2, "البقرة", 2, 1), 4, 2, SurahDownloadState(), {},
                    { actions.onOpenRepeatBottomSheet(2) })
                if (state.value.isRepeatBottomSheetVisible) QuranRepeatBottomSheet(state.value, actions)
            }
        }
        compose.onNodeWithText("تكرار وحفظ").performClick()
        compose.onNodeWithText("من آية إلى آية").performClick()
        compose.onNodeWithText("من آية 1").performClick()
        compose.onNodeWithText("من آية 2").performClick()
        compose.onNodeWithText("إلى آية 4").performClick()
        compose.onNodeWithText("إلى آية 3").performClick()
        compose.onNodeWithText("5 مرات").performClick()
        compose.onNodeWithText("بدء التكرار").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(PlaybackScope.CUSTOM_RANGE, submitted?.playbackScope)
            assertEquals(2, submitted?.startAyah)
            assertEquals(3, submitted?.endAyah)
            assertEquals(5, submitted?.repeatCount)
            assertFalse(state.value.isRepeatBottomSheetVisible)
        }
    }

    private open class NoOpQuranActions : QuranActions {
        override fun onOpenRepeatBottomSheet(surahId: Int) = Unit
        override fun onDismissRepeatBottomSheet() = Unit
        override fun onPlaybackScopeChanged(scope: PlaybackScope) = Unit
        override fun onStartAyahChanged(ayah: Int) = Unit
        override fun onEndAyahChanged(ayah: Int) = Unit
        override fun onRepeatCountChanged(count: Int) = Unit
        override fun onStartRepeatClicked() = Unit
        override fun onSurahChanged(surahId: Int) = Unit
        override fun goToJuz(juz: Int) = Unit
        override fun goToBookmark() = Unit
        override fun downloadSurah(suraId: Int) = Unit
        override fun downloadFullQuran() = Unit
        override fun stopAudio() = Unit
        override fun playAyahAudio(suraNo: Int, ayaNo: Int) = Unit
        override fun saveBookmark(surahId: Int, ayahNo: Int) = Unit
        override fun onSurahVisible(surahId: Int) = Unit
    }
}
