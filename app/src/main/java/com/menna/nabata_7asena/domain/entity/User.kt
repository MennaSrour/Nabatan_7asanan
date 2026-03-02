package com.menna.nabata_7asena.domain.entity

data class User(
    val id: String,
    val name: String,
    val gender: Gender,
    val totalStars: Int,
    val currentStreak: Int,
    val treeStage: Int,
    val quranPartsFinished: Int = 0,
    val prayerNotifications: Boolean = true,
    val azkarNotifications: Boolean = true,
    val quranNotifications: Boolean = true,
) {
    enum class Gender { BOY, GIRL }
}