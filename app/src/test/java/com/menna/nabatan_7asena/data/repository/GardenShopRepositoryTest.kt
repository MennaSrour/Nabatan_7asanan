package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.GardenStorage
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GardenShopRepositoryTest {
    private class Memory(var saved: String? = null) : GardenStorage {
        var fail = false
        override fun read() = saved
        override fun write(value: String) { check(!fail) { "Disk full" }; saved = value }
    }
    private suspend fun GardenRepositoryImpl.progress(id: String = "child") = (observe(id).first() as Resource.Success).data

    @Test fun purchasesAndEquippedChoicesRestoreAndSyncInTheExistingSnapshot() = runTest {
        val storage = Memory()
        val uploaded = mutableListOf<GardenProgress>()
        val first = GardenRepositoryImpl(storage, earnedStars = { 100 }) { _, _, value -> uploaded += value }
        first.buy("child", GardenDecoration.BUTTERFLIES)
        first.equip("child", GardenDecoration.BUTTERFLIES, false)
        val restored = GardenRepositoryImpl(storage, earnedStars = { 100 }) { _, _, value -> uploaded += value }
        assertEquals(setOf("butterflies"), restored.progress().ownedItems)
        assertTrue(restored.progress().equippedItems.isEmpty())
        assertEquals(80, gardenShopBalance(100, restored.progress().spentStars))
        assertTrue(restored.sync("child"))
        assertEquals(setOf("butterflies"), uploaded.single().ownedItems)
        assertTrue(uploaded.single().equippedItems.isEmpty())
        assertTrue(restored.progress("other").ownedItems.isEmpty())
    }
    @Test fun failedPersistenceCannotChargeOrUnlockAnItem() = runTest {
        val storage = Memory()
        val repository = GardenRepositoryImpl(storage, earnedStars = { 100 }) { _, _, _ -> }
        storage.fail = true
        try { repository.buy("child", GardenDecoration.LANTERN); fail("Expected save failure") } catch (_: IllegalStateException) { }
        assertEquals(0, repository.progress().spentStars)
        assertTrue(repository.progress().ownedItems.isEmpty())
        assertNull(storage.saved)
        storage.fail = false
        assertTrue(repository.buy("child", GardenDecoration.LANTERN).purchased)
        assertEquals(30, repository.progress().spentStars)
    }
    @Test fun simultaneousPurchasesCannotOverspendTheAvailableStars() = runTest {
        val repository = GardenRepositoryImpl(Memory(), earnedStars = { 20 }) { _, _, _ -> }
        val results = listOf(GardenDecoration.BUTTERFLIES, GardenDecoration.SPARKLES).map { item ->
            async { try { repository.buy("child", item).purchased } catch (_: InsufficientGardenStars) { false } }
        }.awaitAll()
        assertEquals(1, results.count { it })
        assertTrue(repository.progress().spentStars <= 20)
        assertEquals(1, repository.progress().ownedItems.size)
    }
    @Test fun repeatedPurchaseRequestsBuyOnlyAsManyCopiesAsTheBudgetAllows() = runTest {
        val repository = GardenRepositoryImpl(Memory(), earnedStars = { 100 }) { _, _, _ -> }
        val results = (1..10).map { async { try { repository.buy("child", GardenDecoration.BIRDS).purchased } catch (_: InsufficientGardenStars) { false } } }.awaitAll()
        assertEquals(4, results.count { it })
        assertEquals(100, repository.progress().spentStars)
        assertEquals(4, repository.progress().quantities["birds"])
    }
    @Test fun usesCurrentProfileBudgetAndRejectsAChangedProfile() = runTest {
        var earned: Int? = 10
        val repository = GardenRepositoryImpl(Memory(), earnedStars = { earned }) { _, _, _ -> }
        try { repository.buy("child", GardenDecoration.RAINBOW); fail("Expected insufficient budget") } catch (_: InsufficientGardenStars) { }
        earned = null
        try { repository.buy("child", GardenDecoration.FLOWERS); fail("Expected profile mismatch") } catch (_: IllegalStateException) { }
        assertTrue(repository.progress().ownedItems.isEmpty())
    }
    @Test fun preShopSnapshotsUpgradeWithoutLosingTheGarden() = runTest {
        val storage = Memory("""{"version":1,"deviceId":"old-device","profiles":{"child":{"xp":8,"waterDrops":2,"revision":1}}}""")
        val repository = GardenRepositoryImpl(storage, earnedStars = { 100 }) { _, _, _ -> }
        repository.buy("child", GardenDecoration.FLOWERS)
        assertEquals(8, repository.progress().xp)
        assertEquals(2, repository.progress().waterDrops)
        assertEquals(10, repository.progress().spentStars)
    }
    @Test fun offlineBackupKeepsPurchasedItemsAndPendingRevision() = runTest {
        val repository = GardenRepositoryImpl(Memory(), earnedStars = { 100 }) { _, _, _ -> error("offline") }
        repository.buy("child", GardenDecoration.SPARKLES)
        repository.placeDecoration("child", GardenDecoration.SPARKLES, 0)
        assertFalse(repository.sync("child"))
        assertEquals(setOf("sparkles"), repository.progress().equippedItems)
        assertTrue(repository.progress().syncPending)
    }
}
