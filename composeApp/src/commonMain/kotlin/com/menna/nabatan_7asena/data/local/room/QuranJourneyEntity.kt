package com.menna.nabatan_7asena.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.menna.nabatan_7asena.domain.entity.QuranJourney
import kotlinx.serialization.json.Json

@Entity(tableName = "quran_journey")
data class QuranJourneyEntity(
    @PrimaryKey val profileId: String,
    val snapshot: String,
    val syncVersion: Int,
    val syncedVersion: Int
) {
    fun toJourney(): QuranJourney = json.decodeFromString(snapshot)

    companion object {
        internal val json = Json { ignoreUnknownKeys = true }
    }
}

internal val CREATE_QURAN_JOURNEY_TABLE = """
    CREATE TABLE IF NOT EXISTS quran_journey (
        profileId TEXT NOT NULL PRIMARY KEY,
        snapshot TEXT NOT NULL,
        syncVersion INTEGER NOT NULL,
        syncedVersion INTEGER NOT NULL
    )
""".trimIndent()

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) = connection.execSQL(CREATE_QURAN_JOURNEY_TABLE)
}
