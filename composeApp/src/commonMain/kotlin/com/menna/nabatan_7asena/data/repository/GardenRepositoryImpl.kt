package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.GardenStorage
import com.menna.nabatan_7asena.data.local.PlatformGardenStorage
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.GardenRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
private data class GardenSnapshot(
    val version: Int = 2,
    val deviceId: String,
    val profiles: Map<String, GardenProgress> = emptyMap(),
    val receivedGifts: Map<String, Set<String>> = emptyMap()
)

@OptIn(ExperimentalUuidApi::class)
class GardenRepositoryImpl(
    private val storage: GardenStorage = PlatformGardenStorage(),
    private val earnedStars: suspend (String) -> Int? = { null },
    private val backup: suspend (String, String, GardenProgress) -> Unit
) : GardenRepository {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val mutex = Mutex()
    private val syncMutex = Mutex()
    private var loadError: Exception? = null
    private val state = MutableStateFlow(readInitial())

    private fun readInitial(): GardenSnapshot = try {
        val snapshot = storage.read()?.let { json.decodeFromString<GardenSnapshot>(it) } ?: GardenSnapshot(deviceId = Uuid.random().toString())
        require(snapshot.version in 1..2 && snapshot.deviceId.isNotBlank())
        snapshot.profiles.values.forEach { it.validate() }
        val migrated = snapshot.copy(version = 2, profiles = snapshot.profiles.mapValues { it.value.asWorld() })
        migrated.profiles.values.forEach { it.validate() }
        migrated
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        loadError = e
        GardenSnapshot(deviceId = Uuid.random().toString())
    }

    override fun observe(profileId: String): Flow<Resource<GardenProgress>> = flow {
        val failure = loadError
        if (failure != null) emit(Resource.Error(failure))
        else emitAll(state.map { Resource.Success(it.profiles[profileId] ?: GardenProgress().asWorld()) }.distinctUntilChanged())
    }

    internal suspend fun creditFriendGifts(profileId: String, receiptIds: Set<String>) = mutex.withLock {
        check(loadError == null) { "Garden storage could not be read" }
        val current = state.value
        val received = current.receivedGifts[profileId].orEmpty()
        val newGifts = receiptIds - received
        if (newGifts.isEmpty()) return@withLock
        val progress = current.profiles[profileId] ?: GardenProgress().asWorld()
        check(progress.waterDrops <= Int.MAX_VALUE - newGifts.size && progress.revision < Long.MAX_VALUE)
        val next = progress.copy(waterDrops = progress.waterDrops + newGifts.size, revision = progress.revision + 1)
        save(current.copy(
            profiles = current.profiles + (profileId to next),
            receivedGifts = current.receivedGifts + (profileId to (received + newGifts))
        ))
    }

    private fun save(snapshot: GardenSnapshot) {
        storage.write(json.encodeToString(GardenSnapshot.serializer(), snapshot))
        state.value = snapshot
    }

    override suspend fun creditTasks(profileId: String, date: LocalDate, completedIds: Set<Int>) =
        update(profileId) { awardGardenTasks(it, date, completedIds) }

    override suspend fun creditWheelWater(profileId: String, date: LocalDate, drops: Int) =
        update(profileId) { progress ->
            val day = date.toString()
            if (day in progress.wheelRewardDays) progress else {
                check(drops > 0 && progress.waterDrops <= Int.MAX_VALUE - drops && progress.revision < Long.MAX_VALUE)
                progress.copy(
                    waterDrops = progress.waterDrops + drops,
                    wheelRewardDays = progress.wheelRewardDays + day,
                    revision = progress.revision + 1
                )
            }
        }

    override suspend fun water(profileId: String, date: LocalDate, treeId: String?): GardenWatering {
        var applied = false
        val progress = update(profileId) {
            applied = it.waterDrops > 0
            waterGarden(it, date, treeId)
        }
        return GardenWatering(progress, applied)
    }

    override suspend fun buy(profileId: String, item: GardenDecoration): GardenPurchase =
        buyProduct(profileId, GardenProduct.entries.first { it.id == item.id })

    override suspend fun buyProduct(profileId: String, product: GardenProduct): GardenPurchase {
        var purchased = false
        val progress = update(profileId) {
            val stars = earnedStars(profileId) ?: error("Garden profile changed or is unavailable")
            buyGardenProduct(it, product, stars, Uuid.random().toString()).also { result -> purchased = result.purchased }.progress
        }
        return GardenPurchase(progress, purchased)
    }

    override suspend fun plantTree(profileId: String, treeId: String, plot: Int?) = update(profileId) { plantGardenTree(it, treeId, plot) }
    override suspend fun showTree(profileId: String, treeId: String, visible: Boolean) = update(profileId) { setGardenTreeVisible(it, treeId, visible) }
    override suspend fun placeDecoration(profileId: String, item: GardenDecoration, plot: Int) =
        update(profileId) { placeGardenDecoration(it, item, plot, Uuid.random().toString()) }
    override suspend fun managePlacement(profileId: String, placementId: String, plot: Int?, visible: Boolean?, remove: Boolean) =
        update(profileId) { manageGardenPlacement(it, placementId, plot, visible, remove) }

    override suspend fun equip(profileId: String, item: GardenDecoration, equipped: Boolean): GardenProgress =
        update(profileId) { equipGardenDecoration(it, item, equipped) }

    private suspend fun update(profileId: String, transform: suspend (GardenProgress) -> GardenProgress): GardenProgress = mutex.withLock {
        check(loadError == null) { "Garden storage could not be read" }
        require(profileId.isNotBlank())
        val current = state.value.profiles[profileId] ?: GardenProgress().asWorld()
        val next = transform(current)
        next.validate()
        if (next != current) {
            val snapshot = state.value.copy(profiles = state.value.profiles + (profileId to next))
            save(snapshot)
        }
        next
    }

    // Backups are serialized and acknowledged only for the exact revision uploaded.
    override suspend fun sync(profileId: String): Boolean = syncMutex.withLock {
        if (profileId == "local" || profileId.isBlank() || loadError != null) return@withLock false
        val snapshot = state.value
        val progress = snapshot.profiles[profileId] ?: return@withLock true
        if (!progress.syncPending) return@withLock true
        try {
            backup(profileId, snapshot.deviceId, progress)
            update(profileId) { it.copy(syncedRevision = maxOf(it.syncedRevision, progress.revision)) }
            !state.value.profiles.getValue(profileId).syncPending
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }
}
