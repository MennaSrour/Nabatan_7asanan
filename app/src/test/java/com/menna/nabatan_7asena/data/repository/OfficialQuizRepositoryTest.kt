package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.*
import com.menna.nabatan_7asena.data.remote.OfficialQuizRemote
import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto
import com.menna.nabatan_7asena.domain.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.*
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfficialQuizRepositoryTest {
    private class Memory(var saved: String? = null) : OfficialAttemptStorage {
        var failWrites = false
        override fun read() = saved
        override fun write(value: String) { check(!failWrites); saved = value }
    }
    private class Remote : OfficialQuizRemote {
        val attempts = mutableMapOf<String, QuizScoreDto>()
        val scores = mutableMapOf<String, QuizScoreDto>()
        var offline = false
        var created = 0
        var finishGate: CompletableDeferred<Unit>? = null
        private val mutex = Mutex()
        private fun key(week: String, user: String) = "$week|$user"
        override suspend fun getOfficialQuizAttempt(weekId: String, userId: String): QuizScoreDto? {
            check(!offline)
            return scores[key(weekId, userId)] ?: attempts[key(weekId, userId)]
        }
        override suspend fun reserveOfficialQuizAttempt(weekId: String, dto: QuizScoreDto) = mutex.withLock {
            check(!offline)
            val key = key(weekId, dto.userId)
            val existing = scores[key] ?: attempts[key]
            if (existing != null) existing to false else { attempts[key] = dto; created++; dto to true }
        }
        override suspend fun finishOfficialQuizAttempt(weekId: String, userId: String, token: String, score: Int): QuizScoreDto {
            check(!offline)
            finishGate?.await()
            return mutex.withLock {
                val key = key(weekId, userId)
                scores[key] ?: attempts.getValue(key).let {
                    check(it.token == token)
                    it.copy(score = score, completed = true).also { saved -> scores[key] = saved; attempts[key] = saved }
                }
            }
        }
    }
    private val remote = Remote()
    private val memory = Memory()
    private var date = LocalDate(2026, 11, 27)
    private var user = User("child", "طفل", User.Gender.BOY, 0, 0, 1)
    private fun repository(storage: Memory = memory) = OfficialQuizRepositoryImpl(remote, { user }, OfficialAttemptStore(storage), { date })

    @Test fun reservationSurvivesRestartAndDisallowsRetaking() = runTest {
        val first = repository().begin()
        assertEquals(first, repository().status())
        try { repository().begin(); fail("Expected used attempt") } catch (e: OfficialAttemptAlreadyUsed) { assertEquals(first, e.attempt) }
        assertEquals(1, remote.created)
    }
    @Test fun anotherDeviceCannotReserveTheSameUserAndWeek() = runTest {
        repository().begin()
        try { repository(Memory()).begin(); fail("Expected remote lock") } catch (_: OfficialAttemptAlreadyUsed) { }
        assertEquals(1, remote.created)
    }
    @Test fun simultaneousReservationsHaveOnlyOneWinner() = runTest {
        val results = listOf(repository(Memory()), repository(Memory())).map { repo ->
            async { try { repo.begin(); true } catch (_: OfficialAttemptAlreadyUsed) { false } }
        }.awaitAll()
        assertEquals(1, results.count { it })
        assertEquals(1, remote.created)
    }
    @Test fun completedScoreCannotBeChangedByAnotherFinishOrRestart() = runTest {
        val repo = repository()
        val attempt = repo.begin()
        assertEquals(3, repo.finish(attempt, 3).score)
        assertEquals(3, repo.finish(attempt, 5).score)
        assertEquals(3, repository().status()?.score)
        assertEquals(3, remote.scores.values.single().score)
    }
    @Test fun offlineFinishIsLockedLocallyAndRetriesCapturedWeekEvenAfterWeekRollover() = runTest {
        val repo = repository()
        val attempt = repo.begin()
        remote.offline = true
        val saved = repo.finish(attempt, 4)
        assertTrue(saved.completed)
        assertFalse(saved.synced)
        assertEquals(saved, repository().status())
        date = LocalDate(2026, 12, 4)
        remote.offline = false
        repository().syncPending()
        assertEquals(4, remote.scores["${attempt.weekId}|child"]?.score)
        assertNull(repository().status())
        assertNotEquals(attempt.weekId, repository().begin().weekId)
    }
    @Test fun distinctProfilesHaveIndependentAttempts() = runTest {
        repository().begin()
        user = user.copy(id = "other")
        assertNull(repository().status())
        repository().begin()
        assertEquals(2, remote.created)
    }
    @Test fun saturdayCannotStartAnOfficialAttempt() = runTest {
        date = LocalDate(2026, 11, 28)
        try { repository().begin(); fail("Expected Friday guard") } catch (_: IllegalStateException) { }
        assertEquals(0, remote.created)
    }
    @Test fun offlineStartDoesNotManufactureALocalReservation() = runTest {
        remote.offline = true
        try { repository().begin(); fail("Expected network failure") } catch (_: IllegalStateException) { }
        assertNull(memory.saved)
        assertEquals(0, remote.created)
    }
    @Test fun serverReservationStillPreventsRetakeIfTheLocalWriteFailed() = runTest {
        memory.failWrites = true
        try { repository().begin(); fail("Expected disk failure") } catch (_: IllegalStateException) { }
        memory.failWrites = false
        try { repository().begin(); fail("Expected reserved attempt") } catch (_: OfficialAttemptAlreadyUsed) { }
        assertEquals(1, remote.created)
    }
    @Test fun cancelledFinishKeepsTheDurableLocalCompletionForRetry() = runTest {
        val repo = repository()
        val attempt = repo.begin()
        remote.finishGate = CompletableDeferred()
        val finish = async { repo.finish(attempt, 2) }
        runCurrent()
        finish.cancelAndJoin()
        assertTrue(repository().status()?.completed == true)
        remote.finishGate?.complete(Unit)
        repo.syncPending()
        assertEquals(2, remote.scores.values.single().score)
    }
    @Test fun legacyCompletedResultsAlsoLockTheOfficialExam() = runTest {
        remote.scores["2026-11-21|child"] = QuizScoreDto(userId = "child", score = 5)
        assertEquals(5, repository().status()?.score)
        try { repository().begin(); fail("Expected legacy result lock") } catch (_: OfficialAttemptAlreadyUsed) { }
        assertEquals(0, remote.created)
    }
    @Test fun malformedLocalAttemptsAreNotSilentlyOverwritten() = runTest {
        memory.saved = "{broken"
        try { repository().begin(); fail("Expected corrupt storage failure") } catch (_: Exception) { }
        assertEquals("{broken", memory.saved)
        assertEquals(0, remote.created)
    }
}
