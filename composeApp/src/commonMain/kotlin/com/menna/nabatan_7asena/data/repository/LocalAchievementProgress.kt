package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.OfficialAttemptStore
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.entity.AchievementProgress
import com.menna.nabatan_7asena.domain.entity.AchievementStats
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class LocalAchievementProgress(
    private val dao: AppDao,
    private val officialAttempts: OfficialAttemptStore
) : AchievementProgress {
    override fun observe(userId: String) = combine(
        dao.observeCompletedDays(100),
        dao.observeCompletedDays(400),
        officialAttempts.observeCompleted(userId).catch { emit(false) },
        ::AchievementStats
    )
}
