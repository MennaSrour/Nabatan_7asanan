package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.GardenStorage
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.GardenProgress
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GardenRepositoryTest {
    private class MemoryStorage(var saved: String? = null) : GardenStorage {
        var failWrite = false
        override fun read() = saved
        override fun write(value: String) { check(!failWrite) { "Disk full" }; saved = value }
    }
    private val day = LocalDate(2026, 11, 21)
    private suspend fun GardenRepositoryImpl.progress(id: String = "child") = (observe(id).first() as Resource.Success).data

    @Test fun restoresProgressAndDeduplicationAndKeepsProfilesIsolated() = runTest {
        val storage = MemoryStorage()
        val first = GardenRepositoryImpl(storage) { _, _, _ -> }
        first.creditTasks("child", day, setOf(1, 2))
        first.water("child", day)
        first.creditTasks("other", day, setOf(1))
        val restored = GardenRepositoryImpl(storage) { _, _, _ -> }
        assertEquals(first.progress(), restored.progress())
        assertEquals(1, restored.creditTasks("child", day, setOf(1, 2)).waterDrops)
        assertEquals(0, restored.progress("other").xp)
        assertEquals(1, restored.progress("other").waterDrops)
    }

    @Test fun failedLocalWriteDoesNotConsumeWaterOrPublishGrowth() = runTest {
        val storage = MemoryStorage()
        val repository = GardenRepositoryImpl(storage) { _, _, _ -> }
        val initial = repository.creditTasks("child", day, setOf(1))
        val persisted = storage.saved
        storage.failWrite = true
        try { repository.water("child", day); fail("Expected storage failure") } catch (_: IllegalStateException) { }
        assertEquals(initial, repository.progress())
        assertEquals(persisted, storage.saved)
        storage.failWrite = false
        assertTrue(repository.water("child", day).applied)
        assertEquals(1, repository.progress().xp)
    }

    @Test fun corruptedStorageIsPreservedAndCannotBeOverwrittenWithAnEmptyGarden() = runTest {
        val storage = MemoryStorage("{broken")
        var backedUp = false
        val repository = GardenRepositoryImpl(storage) { _, _, _ -> backedUp = true }
        assertTrue(repository.observe("child").first() is Resource.Error)
        try { repository.creditTasks("child", day, setOf(1)); fail("Expected load failure") } catch (_: IllegalStateException) { }
        assertEquals("{broken", storage.saved)
        assertFalse(repository.sync("child"))
        assertFalse(backedUp)
    }

    @Test fun concurrentTapsNeverSpendMoreWaterThanWasEarned() = runTest {
        val repository = GardenRepositoryImpl(MemoryStorage()) { _, _, _ -> }
        repository.creditTasks("child", day, setOf(1, 2))
        val results = (1..20).map { async { repository.water("child", day) } }.awaitAll()
        assertEquals(2, results.count { it.applied })
        assertEquals(2, repository.progress().xp)
        assertEquals(0, repository.progress().waterDrops)
    }

    @Test fun failedBackupKeepsLocalProgressAndDirtyRevisionForRetry() = runTest {
        var offline = true
        var attempts = 0
        val repository = GardenRepositoryImpl(MemoryStorage()) { _, _, _ -> attempts++; check(!offline) }
        val earned = repository.creditTasks("child", day, setOf(1))
        assertFalse(repository.sync("child"))
        assertEquals(earned, repository.progress())
        offline = false
        assertTrue(repository.sync("child"))
        assertFalse(repository.progress().syncPending)
        assertTrue(repository.sync("child"))
        assertEquals(2, attempts)
    }

    @Test fun uploadAcknowledgesOnlyItsOwnRevisionWhenWateringOccursDuringSync() = runTest {
        val gate = CompletableDeferred<Unit>()
        val uploads = mutableListOf<GardenProgress>()
        val repository = GardenRepositoryImpl(MemoryStorage()) { _, _, progress ->
            uploads += progress
            if (uploads.size == 1) gate.await()
        }
        repository.creditTasks("child", day, setOf(1, 2))
        val sync = async { repository.sync("child") }
        runCurrent()
        repository.water("child", day)
        gate.complete(Unit)
        assertFalse(sync.await())
        assertEquals(1L, repository.progress().syncedRevision)
        assertEquals(2L, repository.progress().revision)
        assertTrue(repository.sync("child"))
        assertEquals(listOf(0, 1), uploads.map { it.xp })
        assertEquals(1, repository.progress().waterDrops)
    }

    @Test fun guestNeverUploadsAndRestartsKeepTheSameDeviceBackupIdentity() = runTest {
        val storage = MemoryStorage()
        val paths = mutableListOf<Pair<String, String>>()
        val first = GardenRepositoryImpl(storage) { id, device, _ -> paths += id to device }
        first.creditTasks("local", day, setOf(1))
        assertFalse(first.sync("local"))
        assertTrue(paths.isEmpty())
        first.creditTasks("child", day, setOf(1))
        first.sync("child")
        val restored = GardenRepositoryImpl(storage) { id, device, _ -> paths += id to device }
        restored.water("child", day)
        restored.sync("child")
        assertEquals(2, paths.size)
        assertEquals(paths[0], paths[1])
    }

    @Test fun cancelledBackupDoesNotAcknowledgeOrLosePendingChanges() = runTest {
        val gate = CompletableDeferred<Unit>()
        val repository = GardenRepositoryImpl(MemoryStorage()) { _, _, _ -> gate.await() }
        repository.creditTasks("child", day, setOf(1))
        val sync = async { repository.sync("child") }
        runCurrent()
        sync.cancelAndJoin()
        assertTrue(repository.progress().syncPending)
        gate.complete(Unit)
        assertTrue(repository.sync("child"))
    }
}
