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
    val wheelRewardDays: Set<String> = emptySet()
) {
    val stage: Int get() = GardenStageThresholds.indexOfLast { xp >= it }
    val growthFraction: Float get() {
        if (stage == GardenStageThresholds.lastIndex) return 1f
        return (xp - GardenStageThresholds[stage]).toFloat() / (GardenStageThresholds[stage + 1] - GardenStageThresholds[stage])
    }
    val syncPending: Boolean get() = revision > syncedRevision

    fun validate() {
        require(xp >= 0 && waterDrops >= 0 && revision >= 0 && syncedRevision in 0..revision)
        require(rewardedTaskIds.isEmpty() || rewardDate != null)
        rewardDate?.let { LocalDate.parse(it) }
        lastWateredDate?.let { LocalDate.parse(it) }
        wheelRewardDays.forEach { LocalDate.parse(it) }
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

fun waterGarden(current: GardenProgress, date: LocalDate): GardenProgress {
    if (current.waterDrops == 0) return current
    check(current.xp < Int.MAX_VALUE && current.revision < Long.MAX_VALUE)
    return current.copy(xp = current.xp + 1, waterDrops = current.waterDrops - 1,
        lastWateredDate = maxOf(current.lastWateredDate ?: "", date.toString()), revision = current.revision + 1)
}
