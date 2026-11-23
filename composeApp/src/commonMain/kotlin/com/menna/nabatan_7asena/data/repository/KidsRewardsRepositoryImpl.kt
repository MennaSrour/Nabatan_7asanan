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
import com.menna.nabatan_7asena.data.remote.dto.SpeedQuestionDto
import com.menna.nabatan_7asena.data.remote.dto.VerseBuilderDto
import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
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
                usedRounds = KidsGame.entries.filter { game -> today.any { it.kind == game.name } }.toSet(),
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

    override suspend fun startRound(profileId: String, day: LocalDate, game: KidsGame): Boolean {
        if (day != AppClock.today()) return false
        val round = DailyRewardEntity(profileId, day.toString(), game.name, "", 0, synced = true, settled = false)
        return dao.claimDailyGiftGame(round, AppClock.dateString(day))
    }

    override suspend fun rewardGame(profileId: String, day: LocalDate, game: KidsGame, stars: Int): Int {
        val payout = stars.coerceIn(0, MAX_GAME_STARS)
        if (day != AppClock.today()) return 0
        return dao.finishDailyGiftGame(profileId, day.toString(), game.name, payout)
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

    private val questionCache by lazy { PlatformPreferences("kids_game_content") }
    private val contentJson = Json { ignoreUnknownKeys = true }

    override suspend fun verses(): List<VerseBuilderItem> = loadContent(
        key = "verse",
        serializer = VerseBuilderDto.serializer(),
        remoteItems = remote::getGameVerses,
        legacyItems = remote::getVerseBuilderItems,
        fallback = localVerses,
        identity = { it.words }
    ) { item ->
        val words = item.words.map(String::trim)
        if (item.reference.isNotBlank() && words.size in 3..12 &&
            words.none(String::isBlank) && words.distinct().size > 1) {
            VerseBuilderItem(item.reference.trim(), words)
        } else null
    }

    override suspend fun questions(game: KidsGame): List<SpeedQuestion> {
        require(game == KidsGame.TRIVIA || game == KidsGame.RIDDLE)
        val key = game.name.lowercase()
        return loadContent(
            key = key,
            serializer = SpeedQuestionDto.serializer(),
            remoteItems = { remote.getGameQuestions(key) },
            legacyItems = if (game == KidsGame.TRIVIA) remote::getSpeedChallengeQuestions else null,
            fallback = if (game == KidsGame.RIDDLE) localRiddles else localQuestions,
            identity = { it.prompt }
        ) { item ->
            val choices = item.choices.map(String::trim)
            val question = item.question.trim()
            if (question.isNotEmpty() && choices.size in 2..4 && choices.size == choices.distinct().size &&
                choices.none(String::isBlank) && item.correctIndex in choices.indices) {
                SpeedQuestion(question, choices, item.correctIndex)
            } else null
        }
    }

    private suspend fun <Dto, Item, Key> loadContent(
        key: String,
        serializer: KSerializer<Dto>,
        remoteItems: suspend () -> List<Dto>,
        legacyItems: (suspend () -> List<Dto>)?,
        fallback: List<Item>,
        identity: (Item) -> Key,
        decode: (Dto) -> Item?
    ): List<Item> {
        val listSerializer = ListSerializer(serializer)
        val cached = runCatching {
            questionCache.getString(key)?.let { contentJson.decodeFromString(listSerializer, it) }
                .orEmpty().mapNotNull(decode)
        }.getOrDefault(emptyList())
        val fresh = fetch {
            val items = safelyFetch(remoteItems).orEmpty().filter { decode(it) != null }
            if (items.isNotEmpty() || cached.isNotEmpty()) items
            else legacyItems?.invoke().orEmpty().filter { decode(it) != null }
        }.orEmpty()
        if (fresh.isNotEmpty()) {
            runCatching { questionCache.putString(key, contentJson.encodeToString(listSerializer, fresh)) }
        }
        val available = if (fresh.isNotEmpty()) fresh.mapNotNull(decode) else cached
        val unique = available.distinctBy(identity)
        return (unique + fallback.shuffled()).distinctBy(identity).take(maxOf(fallback.size, unique.size))
    }

    private suspend fun <T> fetch(block: suspend () -> T): T? =
        withTimeoutOrNull(3_000) { safelyFetch(block) }

    private suspend fun <T> safelyFetch(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }

    private fun decodePrize(code: String) = WheelPrize.fromStoredCode(code)

    private companion object {
        const val LOCAL = "local"
        const val WHEEL = "wheel"
        const val MAX_GAME_STARS = 10
    }
}
