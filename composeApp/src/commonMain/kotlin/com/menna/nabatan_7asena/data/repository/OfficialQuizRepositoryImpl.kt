package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.OfficialAttemptStore
import com.menna.nabatan_7asena.data.remote.OfficialQuizRemote
import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.OfficialQuizRepository
import com.menna.nabatan_7asena.domain.util.QuizWeek
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class OfficialQuizRepositoryImpl(
    private val remote: OfficialQuizRemote,
    private val currentUser: suspend () -> User,
    private val store: OfficialAttemptStore = OfficialAttemptStore(),
    private val today: () -> LocalDate = QuizWeek::today
) : OfficialQuizRepository {
    private val mutex = Mutex()
    private fun QuizScoreDto.attempt(week: String) = OfficialQuizAttempt(week, userId, token, score, completed)

    override suspend fun status(): OfficialQuizAttempt? = mutex.withLock {
        val user = currentUser()
        if (user.id == "local" || user.id.isBlank()) return@withLock null
        val week = QuizWeek.weekIdFor(today())
        store.get(user.id, week)?.let { return@withLock it }
        remote.getOfficialQuizAttempt(week, user.id)?.attempt(week)?.also(store::put)
    }

    override suspend fun begin(): OfficialQuizAttempt = mutex.withLock {
        val date = today()
        check(QuizWeek.isCompetitionDay(date)) { "Official challenge is available on Friday only" }
        val user = currentUser()
        check(user.id.isNotBlank() && user.id != "local") { "A saved profile is required" }
        val week = QuizWeek.weekIdFor(date)
        store.get(user.id, week)?.let { throw OfficialAttemptAlreadyUsed(it) }
        val dto = QuizScoreDto(user.id, user.name, user.gender.name, user.avatarIndex, score = 0,
            token = Uuid.random().toString(), completed = false)
        val (saved, created) = remote.reserveOfficialQuizAttempt(week, dto)
        val attempt = saved.attempt(week)
        store.put(attempt)
        if (!created) throw OfficialAttemptAlreadyUsed(attempt)
        attempt
    }

    override suspend fun finish(attempt: OfficialQuizAttempt, score: Int): OfficialQuizAttempt = mutex.withLock {
        require(score in 0..5)
        val saved = store.get(attempt.userId, attempt.weekId) ?: error("Official attempt missing")
        check(saved.token == attempt.token) { "Official attempt mismatch" }
        if (saved.completed) return@withLock saved
        val finished = saved.copy(score = score, completed = true, synced = false)
        store.put(finished) // Lock locally before the network write; cancellation cannot allow a retake.
        try {
            withTimeoutOrNull(4000) {
                remote.finishOfficialQuizAttempt(saved.weekId, saved.userId, saved.token, score).attempt(saved.weekId).also(store::put)
            } ?: finished
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { finished }
    }

    override suspend fun syncPending() {
        val pending = mutex.withLock { store.pending(currentUser().id) }
        pending.forEach { attempt ->
            try {
                val remoteAttempt = withTimeoutOrNull(4000) {
                    remote.finishOfficialQuizAttempt(attempt.weekId, attempt.userId, attempt.token, attempt.score).attempt(attempt.weekId)
                } ?: return@forEach
                mutex.withLock { if (store.get(attempt.userId, attempt.weekId)?.token == attempt.token) store.put(remoteAttempt) }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { /* The durable outbox is retried on the next visit. */ }
        }
    }
}
