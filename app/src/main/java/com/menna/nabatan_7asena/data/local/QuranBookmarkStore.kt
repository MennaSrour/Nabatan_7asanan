package com.menna.nabatan_7asena.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class QuranBookmark(val surahId: Int, val ayahNo: Int)


@Singleton
class QuranBookmarkStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBookmark(): QuranBookmark? {
        val surahId = prefs.getInt(KEY_SURAH, -1)
        val ayahNo = prefs.getInt(KEY_AYAH, -1)
        return if (surahId > 0 && ayahNo > 0) QuranBookmark(surahId, ayahNo) else null
    }

    fun saveBookmark(surahId: Int, ayahNo: Int) {
        prefs.edit()
            .putInt(KEY_SURAH, surahId)
            .putInt(KEY_AYAH, ayahNo)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "quran_prefs"
        const val KEY_SURAH = "bookmark_surah"
        const val KEY_AYAH = "bookmark_ayah"
    }
}
