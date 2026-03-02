package com.menna.nabata_7asena.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val dayId: Int,
    val challenge: String,
    val wisdom: String,
    val quranDhuhr: String,
    val quranAsr: String,
    val quranMaghrib: String,
    val riddleQuestion: String,
    val riddleAnswer: String,
    val riddleOptions: String
)