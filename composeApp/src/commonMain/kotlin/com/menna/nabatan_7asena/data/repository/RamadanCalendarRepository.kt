package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.core.util.DateUtils
import com.menna.nabatan_7asena.core.util.RamadanSeason
import com.menna.nabatan_7asena.core.util.SeasonalThemeManager
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.map

class RamadanCalendarRepository(private val dao: AppDao, private val firebase: FirebaseDataSource) {
    fun observe(profileId: String, season: RamadanSeason) = dao.observeRamadanDays(profileId, season.storageKey).map { it.toSet() }

    suspend fun complete(profileId: String, season: RamadanSeason, day: Int) {
        val current = SeasonalThemeManager.ramadan(DateUtils.currentHijriDate())
        check(current != null && current.year == season.year && current.isPreview == season.isPreview &&
            day in 1..current.unlockedThrough)
        dao.completeRamadanDay(season.storageKey, profileId, day, awardStars = !season.isPreview)
    }

    internal fun observeBackups(profileId: String) = dao.observeRamadanRecords(profileId).map { records ->
        records.mapNotNull { record ->
            record.date.removePrefix("ramadan-").toIntOrNull()?.let { it to (-100 - record.itemId) }
        }.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
    }

    internal suspend fun backup(profileId: String, year: Int, completed: Set<Int>) {
        if (year > 0 && completed.isNotEmpty()) firebase.backupRamadanDays(profileId, year, completed)
    }
}

private val RamadanSeason.storageKey: String
    get() = if (isPreview) "ramadan-preview" else "ramadan-$year"
