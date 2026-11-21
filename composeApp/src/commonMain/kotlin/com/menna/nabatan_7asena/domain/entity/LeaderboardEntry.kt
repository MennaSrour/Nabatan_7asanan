package com.menna.nabatan_7asena.domain.entity

data class LeaderboardEntry(
    val userId: String,
    val name: String,
    val gender: User.Gender,
    val totalStars: Int,
    val quranPartsFinished: Int,
    val rank: Int,
    val avatarIndex: Int = 0
)