package com.menna.nabatan_7asena.domain.entity

import kotlin.test.*

class GardenShopTest {
    @Test fun purchaseDeductsPriceAndAddsInventoryWithoutChangingGrowth() {
        val before = GardenProgress(xp = 8, waterDrops = 3)
        val result = buyGardenDecoration(before, GardenDecoration.BUTTERFLIES, 20)
        assertTrue(result.purchased)
        assertEquals(0, gardenShopBalance(20, result.progress.spentStars))
        assertEquals(setOf("butterflies"), result.progress.ownedItems)
        assertTrue(result.progress.equippedItems.isEmpty())
        assertEquals(1, result.progress.availableDecoration("butterflies"))
        assertEquals(8, result.progress.xp)
        assertEquals(3, result.progress.waterDrops)
    }
    @Test fun insufficientBalanceCannotBuyOrCreateNegativeCurrency() {
        val before = GardenProgress(spentStars = 15)
        assertFailsWith<InsufficientGardenStars> { buyGardenDecoration(before, GardenDecoration.BUTTERFLIES, 20) }
        assertEquals(0, gardenShopBalance(10, 15))
        assertTrue(before.ownedItems.isEmpty())
    }
    @Test fun repeatedPurchasesAddCopiesAndVisibilityChangesNeverChargeAgain() {
        val first = buyGardenDecoration(GardenProgress(), GardenDecoration.LANTERN, 60).progress
        val second = buyGardenDecoration(first, GardenDecoration.LANTERN, 60)
        assertTrue(second.purchased)
        assertEquals(2, second.progress.quantities["lantern"])
        assertEquals(60, second.progress.spentStars)
        val placed = equipGardenDecoration(second.progress, GardenDecoration.LANTERN, true)
        val hidden = equipGardenDecoration(placed, GardenDecoration.LANTERN, false)
        assertTrue(hidden.equippedItems.isEmpty())
        assertEquals(60, hidden.spentStars)
        assertEquals(60, equipGardenDecoration(hidden, GardenDecoration.LANTERN, true).spentStars)
    }
    @Test fun unownedItemsCannotBeEquippedAndMalformedSnapshotsAreRejected() {
        assertFailsWith<IllegalArgumentException> { equipGardenDecoration(GardenProgress(), GardenDecoration.BIRDS, true) }
        assertFailsWith<IllegalArgumentException> { GardenProgress(spentStars = -1).validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(equippedItems = setOf("birds")).validate() }
    }
    @Test fun catalogHasEightDecorationsAndLargeBalancesDoNotOverflow() {
        assertEquals(8, GardenDecoration.entries.map { it.id }.distinct().size)
        assertTrue(GardenDecoration.entries.all { it.price > 0 })
        assertEquals(Int.MAX_VALUE, gardenShopBalance(Int.MAX_VALUE, 0))
    }
}
