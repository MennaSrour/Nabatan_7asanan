package com.menna.nabatan_7asena.domain.entity

enum class GardenDecoration(val id: String, val price: Int) {
    BUTTERFLIES("butterflies", 20), BIRDS("birds", 25), FLOWERS("flowers", 10),
    LANTERN("lantern", 30), RAINBOW("rainbow", 35), SPARKLES("sparkles", 15), BENCH("bench", 25), FOUNTAIN("fountain", 40)
}
class InsufficientGardenStars : IllegalStateException("Insufficient shop stars")
data class GardenPurchase(val progress: GardenProgress, val purchased: Boolean)

fun gardenShopBalance(earnedStars: Int, spentStars: Int): Int =
    (earnedStars.toLong() - spentStars).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()

fun buyGardenDecoration(current: GardenProgress, item: GardenDecoration, earnedStars: Int): GardenPurchase =
    buyGardenProduct(current, GardenProduct.entries.first { it.id == item.id }, earnedStars, "")

/** Compatibility operation; the world editor controls individual placed copies. */
fun equipGardenDecoration(current: GardenProgress, item: GardenDecoration, equipped: Boolean): GardenProgress {
    var world = current.asWorld()
    require(world.quantities.getOrElse(item.id) { 0 } > 0)
    val placed = world.placements.filter { it.itemId == item.id }
    if (placed.isEmpty() && equipped) {
        val plot = (0 until world.plotCount).firstOrNull { p -> world.placements.count { it.plotIndex == p } < GARDEN_DECORATION_SLOTS }
            ?: throw GardenPlacementUnavailable()
        return placeGardenDecoration(world, item, plot, "compat-${item.id}-${world.revision}")
    }
    placed.forEach { world = manageGardenPlacement(world, it.id, visible = equipped) }
    return world
}
