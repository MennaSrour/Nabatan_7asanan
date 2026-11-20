package com.menna.nabatan_7asena.data.remote.dto

import kotlinx.serialization.Serializable
@Serializable
data class DailyPlanDto(
    val wisdom: String = "",
    val quranDhuhr: String = "",
    val quranAsr: String = "",
    val quranMaghrib: String = "",
    val riddleQuestion: String = "",
    val riddleAnswer: String = "",
    val riddleOptions: String = ""
)