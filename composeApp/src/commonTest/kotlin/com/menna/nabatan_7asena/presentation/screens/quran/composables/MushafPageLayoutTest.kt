package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.domain.entity.AyahModel
import kotlin.test.*

class MushafPageLayoutTest {
    @Test fun selectsLargestHalfSpSizeThatFits() {
        val result = fitMushafTypography { size, _ -> size <= 18.3f }
        assertEquals(18f, result.fontSizeSp)
        assertTrue(result.fits)
        assertEquals(26f, fitMushafTypography { _, _ -> true }.fontSizeSp)
    }

    @Test fun compactMinimumPrecedesScrollingFallback() {
        val compact = fitMushafTypography { _, height -> height <= 23f }
        assertEquals(13f, compact.fontSizeSp)
        assertEquals(13f * 1.7f, compact.lineHeightSp)
        assertTrue(compact.fits)
        assertFalse(fitMushafTypography { _, _ -> false }.fits)
    }

    @Test fun midPageSurahBeginsWithBannerAndSeparateBasmalah() {
        val verses = listOf(ayah(93, 11), ayah(94, 1), ayah(94, 2))
        val sections = mushafSections(verses, "سُورَةُ")
        assertIs<MushafSection.Verses>(sections[0])
        assertEquals(94, assertIs<MushafSection.Banner>(sections[1]).surahId)
        assertEquals(MushafSection.Basmalah, sections[2])
        assertEquals(verses, sections.filterIsInstance<MushafSection.Verses>().flatMap { it.ayahs })
    }

    @Test fun fatihaKeepsItsClickableFirstAyahWithoutDuplicateBasmalah() {
        val sections = mushafSections(listOf(ayah(1, 1), ayah(1, 2)), "سُورَةُ")
        assertEquals(0, sections.count { it == MushafSection.Basmalah })
        assertTrue(assertIs<MushafSection.Verses>(sections[1]).centered)
        assertFalse(assertIs<MushafSection.Verses>(sections[2]).centered)
    }

    @Test fun tawbahHasBannerButNoBasmalah() {
        val sections = mushafSections(listOf(ayah(9, 1)), "سُورَةُ")
        assertIs<MushafSection.Banner>(sections.first())
        assertFalse(sections.contains(MushafSection.Basmalah))
    }

    @Test fun finalPageIncludesAllThreeSurahBannersInOrder() {
        val sections = mushafSections(listOf(ayah(112, 1), ayah(113, 1), ayah(114, 1)), "سُورَةُ")
        assertEquals(listOf(112, 113, 114), sections.filterIsInstance<MushafSection.Banner>().map { it.surahId })
        assertEquals(3, sections.count { it == MushafSection.Basmalah })
    }

    @Test fun bookmarkTargetsActiveAyahThenSavedAyahThenPageStart() {
        val verses = listOf(ayah(2, 1), ayah(2, 2), ayah(2, 3))
        assertEquals(verses[2], mushafBookmarkTarget(verses, 2 to 3, 2 to 2))
        assertEquals(verses[1], mushafBookmarkTarget(verses, 3 to 1, 2 to 2))
        assertEquals(verses[0], mushafBookmarkTarget(verses, null, 3 to 1))
        assertNull(mushafBookmarkTarget(emptyList(), null, 0 to 0))
    }

    private fun ayah(surah: Int, number: Int) = AyahModel(
        id = surah * 1000 + number, suraNo = surah, ayaNo = number,
        text = "نص الآية", cleanText = "نص الآية", suraNameAr = "سورة", suraNameEn = "Surah", page = 1, jozz = 1
    )
}
