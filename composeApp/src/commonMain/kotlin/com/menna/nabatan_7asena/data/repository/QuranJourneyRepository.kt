package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.platform.AppClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class QuranJourneyRepository(private val dao: AppDao) {
    val profileId = dao.getUserStats().map { it?.userId?.takeIf(String::isNotBlank) }.distinctUntilChanged()

    fun observe(profileId: String): Flow<QuranJourney> =
        dao.observeQuranJourney(profileId).map { it?.toJourney() ?: QuranJourney() }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeProfile = profileId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else observe(id).map { id to it }
    }

    suspend fun start(profileId: String, goal: WirdGoal) = dao.changeQuranJourney(profileId) {
        if (it.activeKhatmah == null) it.copy(activeKhatmah = Khatmah(goal), celebrationPending = false) else it
    }

    suspend fun complete(profileId: String, date: LocalDate, wird: Wird) {
        if (date != AppClock.today()) return
        dao.completeQuranWird(profileId, date, wird, AppClock.dateString(date),
            AppClock.dateString(date.minus(1, DateTimeUnit.DAY)))
    }

    suspend fun dismissCelebration(profileId: String) = dao.changeQuranJourney(profileId) {
        it.copy(celebrationPending = false)
    }

    suspend fun updateSurah(profileId: String, id: Int, status: MemorizationStatus, ayahs: Int, total: Int) =
        dao.recordSurahMemorization(profileId, id, status, ayahs, total)
}
