package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.GardenStorage
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Test

class GardenWorldRepositoryTest {
    private class Memory(var saved: String? = null) : GardenStorage {
        var fail = false
        override fun read() = saved
        override fun write(value: String) { check(!fail) { "Disk full" }; saved = value }
    }
    private val date = LocalDate(2026, 11, 22)
    private suspend fun GardenRepositoryImpl.progress(id: String = "child") = (observe(id).first() as Resource.Success).data
    private fun repository(storage: Memory = Memory(), backup: suspend (String, String, GardenProgress) -> Unit = { _, _, _ -> }) =
        GardenRepositoryImpl(storage, earnedStars = { 1000 }, backup = backup)

    @Test fun oldSaveMigratesItsTreePurchasesAndLedgerThenWritesAnExplicitVersionTwoSnapshot() = runTest {
        val storage = Memory("""{"version":1,"deviceId":"old-device","profiles":{"child":{"xp":35,"waterDrops":7,"spentStars":50,"ownedItems":["lantern","butterflies"],"equippedItems":["lantern"],"revision":8,"syncedRevision":8,"rewardDate":"2026-11-22","rewardedTaskIds":[1,2]}}}""")
        val uploads = mutableListOf<GardenProgress>()
        val repo = repository(storage) { _, device, progress -> assertEquals("old-device", device); uploads += progress }
        val migrated = repo.progress()
        assertEquals(4, migrated.plotCount)
        assertEquals(35, migrated.trees.single().xp)
        assertEquals(1, migrated.placements.size)
        assertEquals(1, migrated.quantities["butterflies"])
        assertEquals(50, migrated.spentStars)
        assertEquals(7, repo.creditTasks("child", date, setOf(1, 2)).waterDrops)
        repo.plantTree("child", "original-tree", 2)
        assertTrue(storage.saved!!.contains("\"version\":2"))
        assertTrue(repo.sync("child"))
        assertEquals(2, uploads.single().trees.single().plotIndex)
        assertEquals(35, repository(storage).progress().xp)
    }

    @Test fun plantingWateringAndReturningAnIndividualTreeSurviveRestart() = runTest {
        val storage = Memory()
        val repo = repository(storage)
        repo.creditTasks("child", date, setOf(1, 2, 3))
        repo.buyProduct("child", GardenProduct.LAND)
        repo.buyProduct("child", GardenProduct.PALM)
        val palm = repo.progress().trees.single { it.species == GardenSpecies.PALM }
        repo.plantTree("child", palm.id, 1)
        repo.water("child", date, palm.id)
        repo.water("child", date, palm.id)
        repo.plantTree("child", palm.id, null)
        val restored = repository(storage)
        assertEquals(2, restored.progress().trees.single { it.id == palm.id }.xp)
        assertNull(restored.progress().trees.single { it.id == palm.id }.plotIndex)
        assertEquals(0, restored.progress().trees.single { it.id == "original-tree" }.xp)
        assertEquals(1, restored.progress().waterDrops)
        assertEquals(65, restored.progress().spentStars)
        assertEquals(1, restored.progress("other").trees.size)
    }

    @Test fun concurrentPlacementCannotPlaceMoreCopiesThanAreOwned() = runTest {
        val repo = repository()
        repo.buy("child", GardenDecoration.FOUNTAIN)
        val results = (1..5).map { async {
            try { repo.placeDecoration("child", GardenDecoration.FOUNTAIN, 0); true } catch (_: GardenPlacementUnavailable) { false }
        } }.awaitAll()
        assertEquals(1, results.count { it })
        assertEquals(1, repo.progress().placements.size)
        assertEquals(0, repo.progress().availableDecoration("fountain"))
        assertEquals(40, repo.progress().spentStars)
    }

    @Test fun placementManagementAndExpansionRoundTripInBackupsAndLocalStorage() = runTest {
        val storage = Memory()
        val uploads = mutableListOf<GardenProgress>()
        val repo = repository(storage) { _, _, progress -> uploads += progress }
        repeat(2) { repo.buyProduct("child", GardenProduct.LAND) }
        repeat(3) { repo.buy("child", GardenDecoration.LANTERN) }
        repo.placeDecoration("child", GardenDecoration.LANTERN, 1)
        val id = repo.progress().placements.single().id
        repo.managePlacement("child", id, visible = false)
        repo.managePlacement("child", id, plot = 2)
        repo.sync("child")
        val restored = repository(storage).progress()
        assertEquals(3, restored.plotCount)
        assertEquals(3, restored.quantities["lantern"])
        assertEquals(2, restored.placements.single().plotIndex)
        assertFalse(restored.placements.single().visible)
        assertEquals(restored.placements, uploads.last().placements)
        repo.managePlacement("child", id, remove = true)
        assertEquals(3, repo.progress().availableDecoration("lantern"))
        assertEquals(190, repo.progress().spentStars)
    }

    @Test fun failedPlacementSaveCannotMoveATreeOrLoseAnInventoryCopy() = runTest {
        val storage = Memory()
        val repo = repository(storage)
        repo.creditTasks("child", date, setOf(1))
        repo.buyProduct("child", GardenProduct.LAND)
        val before = repo.progress()
        val persisted = storage.saved
        storage.fail = true
        try { repo.plantTree("child", "original-tree", 1); fail("Expected write failure") } catch (_: IllegalStateException) { }
        assertEquals(before, repo.progress())
        assertEquals(persisted, storage.saved)
    }

    @Test fun concurrentExpansionPastSixteenChargesOnlyWhatTheBalanceCovers() = runTest {
        val storage = Memory()
        val repo = repository(storage)
        repeat(19) { repo.buyProduct("child", GardenProduct.LAND) }
        val results = (1..3).map { async {
            try { repo.buyProduct("child", GardenProduct.LAND); true } catch (_: InsufficientGardenStars) { false }
        } }.awaitAll()
        assertEquals(1, results.count { it })
        assertEquals(21, repo.progress().plotCount)
        assertEquals(1000, repo.progress().spentStars)
        assertEquals(21, repository(storage).progress().plotCount)
    }

    @Test fun unsupportedFutureSaveIsPreservedRatherThanReset() = runTest {
        val storage = Memory("""{"version":3,"deviceId":"future","profiles":{}}""")
        val repo = repository(storage)
        assertTrue(repo.observe("child").first() is Resource.Error)
        try { repo.buyProduct("child", GardenProduct.APPLE); fail("Expected load failure") } catch (_: IllegalStateException) { }
        assertEquals("""{"version":3,"deviceId":"future","profiles":{}}""", storage.saved)
    }
}
