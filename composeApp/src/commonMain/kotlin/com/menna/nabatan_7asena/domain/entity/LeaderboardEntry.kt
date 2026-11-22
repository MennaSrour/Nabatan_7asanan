package com.menna.nabatan_7asena.domain.entity

data class LeaderboardEntry(
    val userId: String,
    val name: String,
    val gender: User.Gender,
    val totalStars: Int,
    val completedKhatmahs: Int = 0,
    val rank: Int,
    val avatarIndex: Int = 0
)