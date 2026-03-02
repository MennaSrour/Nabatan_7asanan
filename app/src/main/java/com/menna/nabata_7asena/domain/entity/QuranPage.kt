package com.menna.nabata_7asena.domain.entity

data class QuranPageData(
    val ayahs: List<AyahItem>,
    val surahName: String,
    val surahNo: Int,
    val juz: Int,
    val isNewSurahStart: Boolean
)

data class AyahItem(
    val aya_text: String,
    val aya_no: Int
)