package com.menna.nabatan_7asena.domain.entity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

data class GardenWatering(val progress: GardenProgress, val applied: Boolean)

val GardenStageThresholds = listOf(0, 2, 8, 18, 35, 60)

@Serializable
data class GardenProgress(
    val xp: Int = 0,
    val waterDrops: Int = 0,
    val rewardDate: String? = null,
    val rewardedTaskIds: Set<Int> = emptySet(),
    val lastWateredDate: String? = null,
    val revision: Long = 0,
    val syncedRevision: Long = 0,
    val wheelRewardDays: Set<String> = emptySet(),
    val spentStars: Int = 0,
    val ownedItems: Set<String> = emptySet(),
    val equippedItems: Set<String> = emptySet(),
    val worldFormat: Int = 0,
    val trees: List<GardenTree> = emptyList(),
    val quantities: Map<String, Int> = emptyMap(),
    val placements: List<GardenPlacement> = emptyList(),
    val expansions: Int = 0
) {
    val stage: Int get() = GardenStageThresholds.indexOfLast { xp >= it }
    val growthFraction: Float get() {
        if (stage == GardenStageThresholds.lastIndex) return 1f
        return (xp - GardenStageThresholds[stage]).toFloat() / (GardenStageThresholds[stage + 1] - GardenStageThresholds[stage])
    }
    val syncPending: Boolean get() = revision > syncedRevision

    fun validate() {
        require(spentStars >= 0 && ownedItems.containsAll(equippedItems) && ownedItems.all { it.isNotBlank() })
        require(xp >= 0 && waterDrops >= 0 && revision >= 0 && syncedRevision in 0..revision)
        require(rewardedTaskIds.isEmpty() || rewardDate != null)
        rewardDate?.let { LocalDate.parse(it) }
        lastWateredDate?.let { LocalDate.parse(it) }
        wheelRewardDays.forEach { LocalDate.parse(it) }
        require(worldFormat in 0..2)
        if (worldFormat != 0) {
            val maxExpansions = if (worldFormat == 1) (Int.MAX_VALUE - 4) / 3 else Int.MAX_VALUE - GARDEN_INITIAL_PLOTS
            require(expansions in 0..maxExpansions && trees.isNotEmpty())
            require(trees.map { it.id }.distinct().size == trees.size && trees.all { it.id.isNotBlank() && it.xp >= 0 })
            require(trees.sumOf { it.xp.toLong() } == xp.toLong())
            val planted = trees.mapNotNull { it.plotIndex }
            require(planted.distinct().size == planted.size && planted.all { it in 0 until plotCount })
            trees.forEach { tree -> tree.lastWateredDate?.let { LocalDate.parse(it) } }
            require(quantities.all { it.key.isNotBlank() && it.value > 0 } && ownedItems == quantities.keys)
            require(placements.map { it.id }.distinct().size == placements.size)
            require(placements.all { it.id.isNotBlank() && it.plotIndex in 0 until plotCount && it.slot in 0 until GARDEN_DECORATION_SLOTS })
            require(placements.map { it.plotIndex to it.slot }.distinct().size == placements.size)
            require(placements.groupingBy { it.itemId }.eachCount().all { it.value <= quantities.getOrElse(it.key) { 0 } })
            require(equippedItems == placements.filter { it.visible }.map { it.itemId }.toSet())
        }
    }
}

fun awardGardenTasks(current: GardenProgress, date: LocalDate, completedIds: Set<Int>): GardenProgress {
    val day = date.toString()
    if (current.rewardDate != null && day < current.rewardDate) return current
    val credited = if (day == current.rewardDate) current.rewardedTaskIds else emptySet()
    val newIds = completedIds - credited
    if (newIds.isEmpty()) return current
    check(current.waterDrops <= Int.MAX_VALUE - newIds.size && current.revision < Long.MAX_VALUE)
    return current.copy(waterDrops = current.waterDrops + newIds.size, rewardDate = day,
        rewardedTaskIds = credited + newIds, revision = current.revision + 1)
}

fun waterGarden(current: GardenProgress, date: LocalDate, treeId: String? = null): GardenProgress {
    if (current.waterDrops == 0) return current
    val world = current.asWorld()
    val tree = if (treeId == null) world.trees.firstOrNull { it.plotIndex != null && it.visible }
        else world.trees.firstOrNull { it.id == treeId && it.plotIndex != null && it.visible }
    if (tree == null) throw GardenPlacementUnavailable()
    check(world.xp < Int.MAX_VALUE && world.revision < Long.MAX_VALUE)
    return world.copy(xp = world.xp + 1, waterDrops = world.waterDrops - 1,
        lastWateredDate = maxOf(world.lastWateredDate ?: "", date.toString()),
        trees = world.trees.map { if (it.id == tree.id) it.copy(xp = it.xp + 1, lastWateredDate = maxOf(it.lastWateredDate ?: "", date.toString())) else it },
        revision = world.revision + 1)
}
