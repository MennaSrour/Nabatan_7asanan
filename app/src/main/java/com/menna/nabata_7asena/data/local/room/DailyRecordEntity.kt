package com.menna.nabata_7asena.data.local.room

import androidx.room.Entity

@Entity(
    tableName = "daily_records",
    primaryKeys = ["date", "itemId"]
)
data class DailyRecordEntity(
    val date: String,
    val itemId: Int,
    val isCompleted: Boolean
)