package com.menna.nabatan_7asena.domain.entity

data class User(
    val id: String,
    val name: String,
    val gender: Gender,
    val totalStars: Int,
    val currentStreak: Int,
    val treeStage: Int,
    val quranPartsFinished: Int = 0,
    val avatarIndex: Int = 0,
    val prayerNotifications: Boolean = true,
    val azkarNotifications: Boolean = true,
    val quranNotifications: Boolean = true,
    val completedKhatmahs: Int = 0,
) {
    enum class Gender { BOY, GIRL }
}

const val PRAYER_GUIDE_BONUS_STARS = 5
const val DAILY_COMPLETION_BONUS_STARS = 50
