package com.menna.nabata_7asena.data.remote.dto

data class UserRankDto(
    val userId: String = "",
    val name: String = "",
    val gender: String = "BOY",
    val totalStars: Int = 0,
    val quranPartsFinished: Int = 0
)