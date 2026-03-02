package com.menna.nabata_7asena.domain.entity

data class DailyContent(
    val dayId: Int,
    val challengeTitle: String,
    val wisdom: String,
    val quranWerd: QuranWerd,
    val riddle: Riddle? = null
) {
    data class QuranWerd(
        val dhuhr: String,
        val asr: String,
        val maghrib: String
    )

    data class Riddle(
        val question: String,
        val answer: String,
        val options: List<String>
    )
}