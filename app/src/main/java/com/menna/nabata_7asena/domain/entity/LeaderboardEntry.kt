package com.menna.nabata_7asena.domain.entity

data class LeaderboardEntry(
    val userId: String,
    val name: String,
    val gender: User.Gender,
    val totalStars: Int,
    val quranPartsFinished: Int,
    val rank: Int
)