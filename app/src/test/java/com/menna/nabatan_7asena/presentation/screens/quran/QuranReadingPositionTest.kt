package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.domain.entity.AyahModel
import org.junit.Assert.*
import org.junit.Test

class QuranReadingPositionTest {
    private val ayahs = listOf(ayah(page = 2, juz = 1), ayah(page = 3, juz = 2))

    @Test fun basmalaDoesNotAdvanceReadingPosition() {
        assertEquals(QuranReadingPosition(2, 1), readingPosition(ayahs, 2, 0))
        assertEquals(QuranReadingPosition(2, 1), readingPosition(ayahs, 2, 1))
        assertEquals(QuranReadingPosition(3, 2), readingPosition(ayahs, 2, 2))
    }

    @Test fun surahsWithoutExtraBasmalaUseVisibleIndexDirectly() {
        for (surah in listOf(1, 9)) {
            assertEquals(QuranReadingPosition(3, 2), readingPosition(ayahs, surah, 1))
        }
    }

    @Test fun transientOutOfRangeIndicesAreClamped() {
        assertEquals(QuranReadingPosition(2, 1), readingPosition(ayahs, 2, -1))
        assertEquals(QuranReadingPosition(3, 2), readingPosition(ayahs, 2, 99))
    }

    @Test fun unloadedSurahDoesNotReplaceSavedPosition() {
        assertNull(readingPosition(emptyList(), 2, 0))
    }

    private fun ayah(page: Int, juz: Int) = AyahModel(
        id = page, suraNo = 2, ayaNo = page, text = "", cleanText = "",
        suraNameAr = "", suraNameEn = "", page = page, jozz = juz
    )
}
