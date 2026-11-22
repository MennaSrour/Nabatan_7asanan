package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface KidsRewardsRepository {
    fun observe(profileId: String, day: LocalDate): Flow<DailyRewardStatus>
    suspend fun spin(profileId: String, day: LocalDate): WheelPrize?
    suspend fun rewardGame(profileId: String, day: LocalDate, game: KidsGame, stars: Int): Int
    suspend fun sync(profileId: String, day: LocalDate)
    suspend fun verses(): List<VerseBuilderItem>
    suspend fun questions(): List<SpeedQuestion>
}
