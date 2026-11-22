package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.domain.Surah
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.domain.entity.QuranJourney
import kotlinx.datetime.LocalDate

data class QuranJourneyUiState(val profileId: String, val progress: QuranJourney, val today: LocalDate)

internal data class MemorizationSurah(val surah: Surah, val ayahCount: Int, val juzs: Set<Int>)

/** A Surah spanning several Juzs shares the same progress wherever it appears. */
internal fun memorizationCatalog(pages: Map<Int, List<AyahModel>>): List<MemorizationSurah> {
    val ayahsBySurah = pages.values.flatten().groupBy { it.suraNo }
    return QuranConstants.surahs.mapNotNull { surah ->
        val ayahs = ayahsBySurah[surah.id] ?: return@mapNotNull null
        MemorizationSurah(surah, ayahs.size, ayahs.map { it.jozz }.toSet())
    }
}
