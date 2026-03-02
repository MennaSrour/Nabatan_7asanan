package com.menna.nabata_7asena.data.remote.dto

data class DailyPlanDto(
    val challenge: String = "",
    val wisdom: String = "",
    val quranDhuhr: String = "",
    val quranAsr: String = "",
    val quranMaghrib: String = "",
    val riddleQuestion: String = "",
    val riddleAnswer: String = "",
    val riddleOptions: String = ""
)