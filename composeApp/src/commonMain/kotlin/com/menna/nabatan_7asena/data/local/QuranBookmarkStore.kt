package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QuranBookmark(val surahId: Int, val ayahNo: Int)
class QuranBookmarkStore {
    private val prefs = PlatformPreferences("quran_prefs")
    private val _bookmark = MutableStateFlow(readBookmark())
    val bookmark = _bookmark.asStateFlow()
    private fun readBookmark(): QuranBookmark? {
        val surah = prefs.getInt("bookmark_surah", -1)
        val ayah = prefs.getInt("bookmark_ayah", -1)
        return if (surah > 0 && ayah > 0) QuranBookmark(surah, ayah) else null
    }
    fun getBookmark() = _bookmark.value
    fun saveBookmark(surahId: Int, ayahNo: Int) {
        prefs.putInt("bookmark_surah", surahId)
        prefs.putInt("bookmark_ayah", ayahNo)
        _bookmark.value = QuranBookmark(surahId, ayahNo)
    }
}
