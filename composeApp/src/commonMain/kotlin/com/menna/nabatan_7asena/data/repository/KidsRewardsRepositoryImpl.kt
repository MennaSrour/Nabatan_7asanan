package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.DailyRewardEntity
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.data.remote.dto.WheelClaimDto
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.GardenRepository
import com.menna.nabatan_7asena.domain.repository.KidsRewardsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.combine
import com.menna.nabatan_7asena.domain.Resource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDate
import com.menna.nabatan_7asena.platform.AppClock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KidsRewardsRepositoryImpl(
    private val dao: AppDao,
    private val remote: FirebaseDataSource,
    private val garden: GardenRepository
) : KidsRewardsRepository {
    private val syncLock = Mutex()
    private val wheelLock = Mutex()

    override fun observe(profileId: String, day: LocalDate) =
        combine(dao.observeRewards(profileId, day.toString()), garden.observe(profileId)) { records, progress ->
            val today = records.filter { it.day == day.toString() }
            val wheel = today.firstOrNull { it.kind == WHEEL }
            DailyRewardStatus(
                prize = wheel?.takeIf { it.settled }?.let { decodePrize(it.prize) },
                rewardedGames = KidsGame.entries.filter { game -> today.any { it.kind == game.name } }.toSet(),
                earnedHere = wheel?.earnedHere == true,
                waterDelivered = (progress as? Resource.Success)?.data?.wheelRewardDays?.contains(wheel?.day) == true,
                spinPending = wheel?.settled == false,
                clockRolledBack = records.any { it.kind == WHEEL && it.day > day.toString() }
            )
        }

    override suspend fun spin(profileId: String, day: LocalDate): WheelPrize? = wheelLock.withLock {
        if (day != AppClock.today()) return@withLock null
        val prize = WheelPrize.segments.random()
        val reservation = DailyRewardEntity(
            profileId, day.toString(), WHEEL, prize.name, 0, synced = false,
            settled = false, claimId = Uuid.random().toString()
        )
        if (!dao.reserveDailyGift(reservation, AppClock.dateString(day))) return@withLock null
        val settled = finalizeWheel(reservation)
        if (settled.earnedHere) decodePrize(settled.prize) else null
    }

    // The Room reservation survives a process exit while a server transaction is in flight.
    private suspend fun finalizeWheel(reservation: DailyRewardEntity): DailyRewardEntity {
        val claim = if (reservation.profileId == LOCAL) null else fetch {
            remote.claimWheel(reservation.profileId, reservation.day, WheelClaimDto(reservation.prize, reservation.claimId))
        }
        val earnedHere = claim == null || claim.claimId == reservation.claimId
        val prize = checkNotNull(decodePrize(if (earnedHere) reservation.prize else checkNotNull(claim).prize))
        val settled = reservation.copy(
            prize = prize.name,
            stars = if (earnedHere) prize.stars else 0,
            synced = !earnedHere,
            earnedHere = earnedHere,
            settled = true
        )
        return withContext(NonCancellable) {
            dao.settleReward(settled)
            if (earnedHere) deliverWater(settled)
            settled
        }
    }

    override suspend fun rewardGame(profileId: String, day: LocalDate, game: KidsGame, stars: Int): Int {
        val payout = stars.coerceIn(0, MAX_GAME_STARS)
        if (payout == 0) return 0
        if (day != AppClock.today()) return 0
        val record = DailyRewardEntity(profileId, day.toString(), game.name, "", payout, synced = true)
        return if (dao.claimDailyGiftGame(record, AppClock.dateString(day))) payout else 0
    }

    override suspend fun sync(profileId: String, day: LocalDate) = syncLock.withLock {
        for (pending in dao.pendingRewards(profileId)) {
            val record = if (pending.settled) pending else wheelLock.withLock {
                // Another caller may have settled the same reservation while we waited for it.
                val current = dao.pendingRewards(profileId).firstOrNull { it.claimId == pending.claimId }
                    ?: return@withLock null
                if (current.settled) current else finalizeWheel(current)
            } ?: continue
            if (record.synced) continue
            deliverWater(record)
            val synced = profileId == LOCAL || fetch {
                if (record.kind == WHEEL) {
                    remote.claimWheel(profileId, record.day, WheelClaimDto(record.prize, record.claimId))
                    if ((decodePrize(record.prize)?.water ?: 0) > 0) check(garden.sync(profileId))
                }
                true
            } == true
            if (synced) dao.markRewardSynced(profileId, record.day, record.kind)
        }
        if (dao.latestRewardDate(profileId, WHEEL)?.let { it >= day.toString() } != true) {
            remotePrize(profileId, day)?.let { prize ->
                dao.claimReward(DailyRewardEntity(
                    profileId, day.toString(), WHEEL, prize.name, 0, synced = true, earnedHere = false
                ))
            }
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

    private fun decodePrize(code: String) = WheelPrize.fromStoredCode(code)

    private companion object {
        const val LOCAL = "local"
        const val WHEEL = "wheel"
        const val MAX_GAME_STARS = 10
    }
}
