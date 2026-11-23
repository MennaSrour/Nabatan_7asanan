package com.menna.nabatan_7asena.domain.entity

import kotlinx.datetime.LocalDate
import kotlin.test.*

class GardenWorldTest {
    private val date = LocalDate(2026, 11, 22)

    @Test fun legacyTreeAndPaidDecorationsMigrateWithoutChargingOrLosingGrowth() {
        val legacy = GardenProgress(xp = 35, waterDrops = 7, spentStars = 50, revision = 8, syncedRevision = 8,
            ownedItems = setOf("lantern", "butterflies"), equippedItems = setOf("lantern"), lastWateredDate = "2026-11-21")
        val world = legacy.asWorld()
        world.validate()
        assertEquals(1, world.plotCount)
        assertEquals(35, world.trees.single().xp)
        assertEquals(0, world.trees.single().plotIndex)
        assertEquals("2026-11-21", world.trees.single().lastWateredDate)
        assertEquals(mapOf("lantern" to 1, "butterflies" to 1), world.quantities)
        assertEquals(1, world.placements.size)
        assertEquals(7, world.waterDrops)
        assertEquals(50, world.spentStars)
        assertEquals(8L, world.revision)
        assertEquals(world, world.asWorld())
    }

    @Test fun everyTreeGrowsIndependentlyAndRemovingItKeepsItsGrowth() {
        var world = GardenProgress(waterDrops = 4).asWorld().copy(expansions = 3)
        world = buyGardenProduct(world, GardenProduct.PALM, 200, "palm").progress
        world = plantGardenTree(world, "palm", 1)
        world = waterGarden(world, date, "palm")
        world = waterGarden(world, date, "palm")
        assertEquals(2, world.trees.first { it.id == "palm" }.xp)
        assertEquals(0, world.trees.first { it.id == "original-tree" }.xp)
        assertEquals(2, world.xp)
        world = plantGardenTree(world, "palm", null)
        assertNull(world.trees.first { it.id == "palm" }.plotIndex)
        assertFailsWith<GardenPlacementUnavailable> { waterGarden(world, date, "palm") }
        world = plantGardenTree(world, "palm", 3)
        world = waterGarden(world, date, "original-tree")
        world.validate()
        assertEquals(2, world.trees.first { it.id == "palm" }.xp)
        assertEquals(1, world.trees.first { it.id == "original-tree" }.xp)
    }

    @Test fun newGardenStartsWithOneTreeAndCanExpandBeyondTheOldLimit() {
        var world = GardenProgress().asWorld()
        assertEquals(1, world.plotCount)
        assertEquals(0, world.trees.single().plotIndex)
        repeat(60) { index ->
            world = buyGardenProduct(world, GardenProduct.LAND, 3000, "unused").progress
            assertEquals(index + 2, world.plotCount)
        }
        assertEquals(3000, world.spentStars)
        assertFailsWith<InsufficientGardenStars> { buyGardenProduct(world, GardenProduct.LAND, 3000, "unused") }
        world.validate()
    }

    @Test fun existingExpandedGardensKeepEveryPlotAndPlacement() {
        val legacy = GardenProgress(
            worldFormat = 1,
            expansions = 4,
            spentStars = 200,
            trees = listOf(GardenTree("tree", GardenSpecies.OLIVE, 15))
        )
        val world = legacy.asWorld()
        assertEquals(16, world.plotCount)
        assertEquals(15, world.trees.single().plotIndex)
        assertEquals(200, world.spentStars)
        val expanded = buyGardenProduct(world, GardenProduct.LAND, 250, "unused").progress
        assertEquals(17, expanded.plotCount)
        expanded.validate()
    }

    @Test fun quantitiesCanBePurchasedPlacedHiddenMovedAndReturnedWithoutRefundOrLoss() {
        var world = GardenProgress().asWorld().copy(expansions = 2)
        repeat(3) { world = buyGardenDecoration(world, GardenDecoration.FLOWERS, 100).progress }
        assertEquals(3, world.availableDecoration("flowers"))
        world = placeGardenDecoration(world, GardenDecoration.FLOWERS, 0, "a")
        world = placeGardenDecoration(world, GardenDecoration.FLOWERS, 1, "b")
        world = manageGardenPlacement(world, "a", visible = false)
        assertEquals(1, world.availableDecoration("flowers"))
        world = manageGardenPlacement(world, "a", plot = 2)
        assertFalse(world.placements.first { it.id == "a" }.visible)
        world = manageGardenPlacement(world, "a", remove = true)
        assertEquals(2, world.availableDecoration("flowers"))
        assertEquals(3, world.quantities["flowers"])
        assertEquals(30, world.spentStars)
        assertTrue(world.placements.single().visible)
        world.validate()
    }

    @Test fun invalidPlacementAndUnownedCopiesNeverCreateInventoryOrExtraSlots() {
        var world = GardenProgress().asWorld()
        assertFailsWith<GardenPlacementUnavailable> { placeGardenDecoration(world, GardenDecoration.BENCH, 0, "missing") }
        repeat(4) { world = buyGardenDecoration(world, GardenDecoration.FLOWERS, 100).progress }
        repeat(3) { world = placeGardenDecoration(world, GardenDecoration.FLOWERS, 0, "copy-$it") }
        assertFailsWith<GardenPlacementUnavailable> { placeGardenDecoration(world, GardenDecoration.FLOWERS, 0, "fourth") }
        assertFailsWith<GardenPlacementUnavailable> { placeGardenDecoration(world, GardenDecoration.FLOWERS, 4, "outside") }
        assertEquals(1, world.availableDecoration("flowers"))
        assertFailsWith<GardenPlacementUnavailable> { plantGardenTree(world, "original-tree", 4) }
        world.validate()
    }

    @Test fun hiddenTreesDoNotSpendWaterAndReplantingMakesAnInventoryTreeVisible() {
        var world = GardenProgress(waterDrops = 2).asWorld().copy(expansions = 2)
        world = setGardenTreeVisible(world, "original-tree", false)
        assertFailsWith<GardenPlacementUnavailable> { waterGarden(world, date, "original-tree") }
        assertEquals(2, world.waterDrops)
        world = plantGardenTree(world, "original-tree", null)
        world = plantGardenTree(world, "original-tree", 2)
        assertTrue(world.trees.single().visible)
        assertEquals(0, world.trees.single().xp)
    }

    @Test fun twoTreesCannotOccupyTheSamePlotAndFailedPurchasesDoNotChangeTheWorld() {
        val first = buyGardenProduct(GardenProgress().asWorld(), GardenProduct.OLIVE, 20, "olive").progress
        assertFailsWith<GardenPlacementUnavailable> { plantGardenTree(first, "olive", 0) }
        assertFailsWith<InsufficientGardenStars> { buyGardenProduct(first, GardenProduct.BLOSSOM, 20, "blossom") }
        assertEquals(20, first.spentStars)
        assertEquals(2, first.trees.size)
        first.validate()
    }

    @Test fun malformedWorldSnapshotsAreRejected() {
        val world = GardenProgress().asWorld()
        assertFailsWith<IllegalArgumentException> { world.copy(xp = 5).validate() }
        assertFailsWith<IllegalArgumentException> { world.copy(trees = world.trees + world.trees.single()).validate() }
        assertFailsWith<IllegalArgumentException> { world.copy(expansions = -1).validate() }
        assertFailsWith<IllegalArgumentException> { world.copy(placements = listOf(GardenPlacement("x", "unowned", 0, 0))).validate() }
    }
}
