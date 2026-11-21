package com.menna.nabatan_7asena.domain.entity

enum class GardenDecoration(val id: String, val price: Int) {
    BUTTERFLIES("butterflies", 20), BIRDS("birds", 25), FLOWERS("flowers", 10),
    LANTERN("lantern", 30), RAINBOW("rainbow", 35), SPARKLES("sparkles", 15)
}
class InsufficientGardenStars : IllegalStateException("Insufficient shop stars")
data class GardenPurchase(val progress: GardenProgress, val purchased: Boolean)

fun gardenShopBalance(earnedStars: Int, spentStars: Int): Int =
    (earnedStars.toLong() - spentStars).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()

fun buyGardenDecoration(current: GardenProgress, item: GardenDecoration, earnedStars: Int): GardenPurchase {
    if (item.id in current.ownedItems) return GardenPurchase(current, false)
    if (gardenShopBalance(earnedStars, current.spentStars) < item.price) throw InsufficientGardenStars()
    check(current.revision < Long.MAX_VALUE && current.spentStars <= Int.MAX_VALUE - item.price)
    return GardenPurchase(current.copy(spentStars = current.spentStars + item.price,
        ownedItems = current.ownedItems + item.id, equippedItems = current.equippedItems + item.id,
        revision = current.revision + 1), true)
}

fun equipGardenDecoration(current: GardenProgress, item: GardenDecoration, equipped: Boolean): GardenProgress {
    require(item.id in current.ownedItems) { "Decoration is not owned" }
    val items = if (equipped) current.equippedItems + item.id else current.equippedItems - item.id
    if (items == current.equippedItems) return current
    check(current.revision < Long.MAX_VALUE)
    return current.copy(equippedItems = items, revision = current.revision + 1)
}
