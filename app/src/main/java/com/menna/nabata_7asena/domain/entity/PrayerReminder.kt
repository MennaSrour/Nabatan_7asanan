package com.menna.nabata_7asena.domain.entity

data class PrayerReminder(
    val prayerName: String,
    val timeInMillis: Long,
    val audioResId: Int
)