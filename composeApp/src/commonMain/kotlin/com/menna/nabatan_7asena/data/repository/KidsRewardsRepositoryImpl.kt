package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.DailyRewardEntity
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto
import com.menna.nabatan_7asena.data.remote.dto.WheelClaimDto
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.GardenRepository
import com.menna.nabatan_7asena.domain.repository.KidsRewardsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import com.menna.nabatan_7asena.domain.Resource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDate

class KidsRewardsRepositoryImpl(
    private val dao: AppDao,
    private val remote: FirebaseDataSource,
    private val garden: GardenRepository
) : KidsRewardsRepository {
    private val syncLock = Mutex()

    override fun observe(profileId: String, day: LocalDate) =
        combine(dao.observeRewards(profileId, day.toString()), garden.observe(profileId)) { records, progress ->
            val wheel = records.firstOrNull { it.kind == WHEEL }
            DailyRewardStatus(
                prize = wheel?.let { decodePrize(it.prize) },
                rewardedGames = KidsGame.entries.filter { game -> records.any { it.kind == game.name } }.toSet(),
                earnedHere = wheel?.earnedHere == true,
                waterDelivered = (progress as? Resource.Success)?.data?.wheelRewardDays?.contains(wheel?.day) == true
            )
        }

    override suspend fun spin(profileId: String, day: LocalDate): WheelPrize? {
        if (dao.latestRewardDate(profileId, WHEEL)?.let { it >= day.toString() } == true) return null
        val backedUp = remotePrize(profileId, day)
        if (backedUp != null) {
            recordExistingSpin(profileId, day, backedUp)
            return null
        }
        val prize = WheelPrize.entries.random()
        return withContext(NonCancellable) {
            val record = DailyRewardEntity(profileId, day.toString(), WHEEL, prize.name, prize.stars, false)
            if (!dao.claimReward(record)) return@withContext null
            deliverWater(record)
            prize
        }
    }

    override suspend fun rewardGame(profileId: String, day: LocalDate, game: KidsGame, stars: Int): Int {
        val payout = stars.coerceIn(0, MAX_GAME_STARS)
        if (payout == 0) return 0
        if (game == KidsGame.BONUS && observe(profileId, day).first().prize != WheelPrize.BONUS_ROUND) return 0
        val record = DailyRewardEntity(profileId, day.toString(), game.name, "", payout, profileId == LOCAL)
        return if (dao.claimReward(record)) payout else 0
    }

    override suspend fun sync(profileId: String, day: LocalDate) = syncLock.withLock {
        for (record in dao.pendingRewards(profileId)) {
            deliverWater(record)
            val synced = profileId == LOCAL || fetch {
                if (record.kind == WHEEL) {
                    remote.backupWheelClaim(profileId, record.day, WheelClaimDto(record.prize))
                    if (decodePrize(record.prize)?.water != 0) check(garden.sync(profileId))
                }
                if (record.stars > 0) {
                    val stats = dao.getUserStats().first()?.takeIf { it.userId == profileId }
                    if (stats != null) {
                        remote.uploadUserScore(UserRankDto(
                            stats.userId, stats.name, stats.gender.lowercase(), stats.totalStars,
                            stats.quranPartsFinished, stats.avatarIndex
                        ))
                    }
                }
                true
            } == true
            if (synced) dao.markRewardSynced(profileId, record.day, record.kind)
        }
        if (dao.latestRewardDate(profileId, WHEEL)?.let { it >= day.toString() } != true) {
            remotePrize(profileId, day)?.let { recordExistingSpin(profileId, day, it) }
        }
    }

    private suspend fun deliverWater(record: DailyRewardEntity) {
        val drops = decodePrize(record.prize)?.water ?: 0
        if (record.kind == WHEEL && drops > 0) {
            garden.creditWheelWater(record.profileId, LocalDate.parse(record.day), drops)
        }
    }

    private suspend fun remotePrize(profileId: String, day: LocalDate): WheelPrize? =
        if (profileId == LOCAL) null else fetch { remote.getWheelClaim(profileId, day.toString()) }
            ?.let { decodePrize(it.prize) }

    private suspend fun recordExistingSpin(profileId: String, day: LocalDate, prize: WheelPrize) {
        dao.claimReward(DailyRewardEntity(profileId, day.toString(), WHEEL, prize.name, 0, synced = true, earnedHere = false))
    }

    override suspend fun verses(): List<VerseBuilderItem> {
        val items = fetch { remote.getVerseBuilderItems() }.orEmpty().mapNotNull { item ->
            val words = item.words.map(String::trim)
            if (item.reference.isNotBlank() && words.size in 3..12 &&
                words.none(String::isBlank) && words.distinct().size > 1) {
                VerseBuilderItem(item.reference, words)
            } else null
        }
        val remoteItems = items.distinctBy { it.words }
        if (remoteItems.isEmpty()) return localVerses
        return (remoteItems + localVerses.shuffled()).distinctBy { it.words }.take(maxOf(5, remoteItems.size))
    }

    override suspend fun questions(): List<SpeedQuestion> {
        val questions = fetch { remote.getSpeedChallengeQuestions() }.orEmpty().mapNotNull { item ->
            val choices = item.choices.map(String::trim)
            if (item.question.isNotBlank() && choices.size in 3..4 && choices.size == choices.distinct().size &&
                choices.none(String::isBlank) && item.correctIndex in choices.indices) {
                SpeedQuestion(item.question, choices, item.correctIndex)
            } else null
        }
        val remoteQuestions = questions.distinctBy { it.prompt }
        if (remoteQuestions.isEmpty()) return localQuestions
        return (remoteQuestions + localQuestions.shuffled()).distinctBy { it.prompt }.take(maxOf(15, remoteQuestions.size))
    }

    private suspend fun <T> fetch(block: suspend () -> T): T? = withTimeoutOrNull(3_000) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private fun decodePrize(code: String) = WheelPrize.entries.firstOrNull { it.name == code }

    private companion object {
        const val LOCAL = "local"
        const val WHEEL = "wheel"
        const val MAX_GAME_STARS = 10
    }
}
