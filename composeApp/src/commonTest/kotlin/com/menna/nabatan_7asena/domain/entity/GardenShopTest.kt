package com.menna.nabatan_7asena.domain.entity

import kotlin.test.*

class GardenShopTest {
    @Test fun purchaseDeductsPriceAndEquipsItemWithoutChangingGrowth() {
        val before = GardenProgress(xp = 8, waterDrops = 3)
        val result = buyGardenDecoration(before, GardenDecoration.BUTTERFLIES, 20)
        assertTrue(result.purchased)
        assertEquals(0, gardenShopBalance(20, result.progress.spentStars))
        assertEquals(setOf("butterflies"), result.progress.ownedItems)
        assertEquals(result.progress.ownedItems, result.progress.equippedItems)
        assertEquals(8, result.progress.xp)
        assertEquals(3, result.progress.waterDrops)
    }
    @Test fun insufficientBalanceCannotBuyOrCreateNegativeCurrency() {
        val before = GardenProgress(spentStars = 15)
        assertFailsWith<InsufficientGardenStars> { buyGardenDecoration(before, GardenDecoration.BUTTERFLIES, 20) }
        assertEquals(0, gardenShopBalance(10, 15))
        assertTrue(before.ownedItems.isEmpty())
    }
    @Test fun duplicatePurchasesAndEquipChangesNeverChargeAgain() {
        val purchased = buyGardenDecoration(GardenProgress(), GardenDecoration.LANTERN, 30).progress
        val duplicate = buyGardenDecoration(purchased, GardenDecoration.LANTERN, 30)
        assertFalse(duplicate.purchased)
        assertEquals(purchased, duplicate.progress)
        val hidden = equipGardenDecoration(purchased, GardenDecoration.LANTERN, false)
        assertTrue(hidden.equippedItems.isEmpty())
        assertEquals(setOf("lantern"), hidden.ownedItems)
        assertEquals(30, hidden.spentStars)
        assertEquals(30, equipGardenDecoration(hidden, GardenDecoration.LANTERN, true).spentStars)
    }
    @Test fun unownedItemsCannotBeEquippedAndMalformedSnapshotsAreRejected() {
        assertFailsWith<IllegalArgumentException> { equipGardenDecoration(GardenProgress(), GardenDecoration.BIRDS, true) }
        assertFailsWith<IllegalArgumentException> { GardenProgress(spentStars = -1).validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(equippedItems = setOf("birds")).validate() }
    }
    @Test fun catalogHasSixUniquePositivePricesAndLargeBalancesDoNotOverflow() {
        assertEquals(6, GardenDecoration.entries.map { it.id }.distinct().size)
        assertTrue(GardenDecoration.entries.all { it.price > 0 })
        assertEquals(Int.MAX_VALUE, gardenShopBalance(Int.MAX_VALUE, 0))
    }
}
