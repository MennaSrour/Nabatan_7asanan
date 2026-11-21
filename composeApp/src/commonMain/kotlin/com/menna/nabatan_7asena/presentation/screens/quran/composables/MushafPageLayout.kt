package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.core.util.ayahMarker
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.domain.util.uthmaniText

internal const val MushafBasmalah = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

internal sealed interface MushafSection {
    val text: String
    data class Banner(val surahId: Int, override val text: String) : MushafSection
    data object Basmalah : MushafSection { override val text = MushafBasmalah }
    data class Verses(val ayahs: List<AyahModel>, val centered: Boolean = false) : MushafSection {
        override val text = ayahs.joinToString(" ") { uthmaniText(it.text, it.ayaNo) + " " + ayahMarker(it.ayaNo) }
    }
}

/** Surah boundaries come from Ayah metadata, including boundaries inside a single page. */
internal fun mushafSections(ayahs: List<AyahModel>, surahPrefix: String): List<MushafSection> = buildList {
    ayahs.groupBy { it.suraNo }.forEach { (surah, verses) ->
        if (verses.first().ayaNo == 1) {
            add(MushafSection.Banner(surah, "$surahPrefix ${QuranConstants.surahs[surah - 1].name}"))
            if (surah !in listOf(1, 9)) add(MushafSection.Basmalah)
        }
        if (surah == 1 && verses.first().ayaNo == 1) {
            add(MushafSection.Verses(verses.take(1), centered = true))
            if (verses.size > 1) add(MushafSection.Verses(verses.drop(1)))
        } else add(MushafSection.Verses(verses))
    }
}

internal data class MushafTypography(val fontSizeSp: Float, val lineHeightSp: Float, val fits: Boolean)

/** Half-sp precision; never ellipsize Quran text or shrink below the readable minimum. */
internal fun fitMushafTypography(fits: (Float, Float) -> Boolean): MushafTypography {
    var low = 28 // 14sp
    var high = 52 // 26sp
    var best: Int? = null
    while (low <= high) {
        val step = (low + high) / 2
        val size = step / 2f
        if (fits(size, size * 1.8f)) { best = step; low = step + 1 }
        else high = step - 1
    }
    best?.let { return MushafTypography(it / 2f, it / 2f * 1.8f, true) }
    return MushafTypography(13f, 13f * 1.7f, fits(13f, 13f * 1.7f))
}

internal fun mushafBookmarkTarget(
    ayahs: List<AyahModel>, active: Pair<Int, Int>?, bookmarked: Pair<Int, Int>
): AyahModel? = ayahs.firstOrNull { active == (it.suraNo to it.ayaNo) }
    ?: ayahs.firstOrNull { bookmarked == (it.suraNo to it.ayaNo) }
    ?: ayahs.firstOrNull()
