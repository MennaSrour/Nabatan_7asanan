package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.domain.entity.AyahModel

data class QuranReadingPosition(val page: Int = 1, val juz: Int = 1)

internal fun readingPosition(ayahs: List<AyahModel>, surahId: Int, visibleIndex: Int): QuranReadingPosition? {
    if (ayahs.isEmpty()) return null
    val basmalaOffset = if (surahId == 1 || surahId == 9) 0 else 1
    val index = (visibleIndex - basmalaOffset).coerceIn(0, ayahs.lastIndex)
    return ayahs[index].let { QuranReadingPosition(it.page, it.jozz) }
}
