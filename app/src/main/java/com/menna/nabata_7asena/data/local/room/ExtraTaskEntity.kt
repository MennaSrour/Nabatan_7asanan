package com.menna.nabata_7asena.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extra_tasks")
data class ExtraTaskEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
    val title: String
)