package com.menna.nabatan_7asena.domain.entity

import kotlinx.serialization.Serializable

const val GARDEN_INITIAL_PLOTS = 1
private const val LEGACY_INITIAL_PLOTS = 4
private const val LEGACY_PLOTS_PER_EXPANSION = 3
const val GARDEN_DECORATION_SLOTS = 3

@Serializable
enum class GardenSpecies(val id: String, val price: Int) {
    PALM("tree_palm", 15), APPLE("tree_apple", 15), OLIVE("tree_olive", 20), BLOSSOM("tree_blossom", 20)
}
@Serializable
data class GardenTree(val id: String, val species: GardenSpecies, val plotIndex: Int? = null,
    val xp: Int = 0, val visible: Boolean = true, val lastWateredDate: String? = null) {
    val stage: Int get() = GardenStageThresholds.indexOfLast { xp >= it }
}
@Serializable
data class GardenPlacement(val id: String, val itemId: String, val plotIndex: Int, val slot: Int, val visible: Boolean = true)

enum class GardenProduct(val id: String) {
    PALM("tree_palm"), APPLE("tree_apple"), OLIVE("tree_olive"), BLOSSOM("tree_blossom"),
    BUTTERFLIES("butterflies"), BIRDS("birds"), FLOWERS("flowers"), LANTERN("lantern"),
    RAINBOW("rainbow"), SPARKLES("sparkles"), BENCH("bench"), FOUNTAIN("fountain"), LAND("land");
    val species get() = GardenSpecies.entries.firstOrNull { it.id == id }
    val decoration get() = GardenDecoration.entries.firstOrNull { it.id == id }
    val price: Int get() = species?.price ?: decoration?.price ?: 50
}
class GardenPlacementUnavailable : IllegalStateException("Garden placement is unavailable")

/** Keep existing plot indices and convert old three-plot expansions to individual plots. */
fun GardenProgress.asWorld(): GardenProgress = when (worldFormat) {
    2 -> this
    1 -> copy(worldFormat = 2, expansions = plotCount - GARDEN_INITIAL_PLOTS)
    0 -> {
        val placed = equippedItems.sorted().mapIndexed { index, item ->
            GardenPlacement("legacy-$item", item, index % LEGACY_INITIAL_PLOTS, index / LEGACY_INITIAL_PLOTS)
        }
        copy(
            worldFormat = 2,
            trees = listOf(GardenTree("original-tree", GardenSpecies.APPLE, 0, xp, lastWateredDate = lastWateredDate)),
            quantities = ownedItems.associateWith { 1 },
            placements = placed,
            expansions = placed.maxOfOrNull { it.plotIndex } ?: 0
        )
    }
    else -> error("Unsupported garden format")
}

val GardenProgress.plotCount: Int
    get() = when (worldFormat) {
        1 -> LEGACY_INITIAL_PLOTS + expansions * LEGACY_PLOTS_PER_EXPANSION
        0 -> GARDEN_INITIAL_PLOTS
        else -> GARDEN_INITIAL_PLOTS + expansions
    }

fun GardenProgress.availableDecoration(itemId: String): Int = (quantities[itemId] ?: 0) - placements.count { it.itemId == itemId }
fun GardenProgress.ownedQuantity(product: GardenProduct): Int = if (product == GardenProduct.LAND) expansions
    else product.species?.let { species -> trees.count { it.species == species } } ?: quantities.getOrElse(product.id) { 0 }

internal fun GardenProgress.worldCopy(trees: List<GardenTree> = this.trees, quantities: Map<String, Int> = this.quantities,
    placements: List<GardenPlacement> = this.placements, expansions: Int = this.expansions,
    spent: Int = spentStars): GardenProgress {
    check(revision < Long.MAX_VALUE)
    return copy(trees = trees, quantities = quantities, placements = placements, expansions = expansions,
        spentStars = spent, ownedItems = quantities.keys, equippedItems = placements.filter { it.visible }.map { it.itemId }.toSet(),
        revision = revision + 1)
}

fun buyGardenProduct(current: GardenProgress, product: GardenProduct, earnedStars: Int, newId: String): GardenPurchase {
    val world = current.asWorld()
    if (gardenShopBalance(earnedStars, world.spentStars) < product.price) throw InsufficientGardenStars()
    check(world.spentStars <= Int.MAX_VALUE - product.price)
    val spent = world.spentStars + product.price
    val next = when {
        product == GardenProduct.LAND -> {
            check(world.plotCount < Int.MAX_VALUE)
            world.worldCopy(expansions = world.expansions + 1, spent = spent)
        }
        product.species != null -> {
            require(newId.isNotBlank() && world.trees.none { it.id == newId })
            world.worldCopy(trees = world.trees + GardenTree(newId, requireNotNull(product.species)), spent = spent)
        }
        else -> {
            val quantity = world.quantities.getOrElse(product.id) { 0 }
            check(quantity < Int.MAX_VALUE)
            world.worldCopy(quantities = world.quantities + (product.id to quantity + 1), spent = spent)
        }
    }
    return GardenPurchase(next, true)
}

fun plantGardenTree(current: GardenProgress, treeId: String, plot: Int?): GardenProgress {
    val world = current.asWorld()
    val tree = world.trees.firstOrNull { it.id == treeId } ?: throw GardenPlacementUnavailable()
    if (plot != null && (plot !in 0 until world.plotCount || world.trees.any { it.id != treeId && it.plotIndex == plot })) throw GardenPlacementUnavailable()
    if (tree.plotIndex == plot) return world
    return world.worldCopy(trees = world.trees.map { if (it.id == treeId) it.copy(plotIndex = plot, visible = if (tree.plotIndex == null && plot != null) true else it.visible) else it })
}

fun setGardenTreeVisible(current: GardenProgress, treeId: String, visible: Boolean): GardenProgress {
    val world = current.asWorld()
    val tree = world.trees.firstOrNull { it.id == treeId } ?: throw GardenPlacementUnavailable()
    if (tree.visible == visible) return world
    return world.worldCopy(trees = world.trees.map { if (it.id == treeId) it.copy(visible = visible) else it })
}

fun placeGardenDecoration(current: GardenProgress, item: GardenDecoration, plot: Int, newId: String): GardenProgress {
    val world = current.asWorld()
    if (plot !in 0 until world.plotCount || world.availableDecoration(item.id) <= 0) throw GardenPlacementUnavailable()
    val slot = (0 until GARDEN_DECORATION_SLOTS).firstOrNull { slot -> world.placements.none { it.plotIndex == plot && it.slot == slot } }
        ?: throw GardenPlacementUnavailable()
    require(newId.isNotBlank() && world.placements.none { it.id == newId })
    return world.worldCopy(placements = world.placements + GardenPlacement(newId, item.id, plot, slot))
}

fun manageGardenPlacement(current: GardenProgress, id: String, plot: Int? = null, visible: Boolean? = null, remove: Boolean = false): GardenProgress {
    val world = current.asWorld()
    val placement = world.placements.firstOrNull { it.id == id } ?: throw GardenPlacementUnavailable()
    if (remove) return world.worldCopy(placements = world.placements.filterNot { it.id == id })
    val target = plot ?: placement.plotIndex
    if (target !in 0 until world.plotCount) throw GardenPlacementUnavailable()
    val slot = if (target == placement.plotIndex) placement.slot else (0 until GARDEN_DECORATION_SLOTS).firstOrNull { slot ->
        world.placements.none { it.id != id && it.plotIndex == target && it.slot == slot }
    } ?: throw GardenPlacementUnavailable()
    val next = placement.copy(plotIndex = target, slot = slot, visible = visible ?: placement.visible)
    if (next == placement) return world
    return world.worldCopy(placements = world.placements.map { if (it.id == id) next else it })
}
