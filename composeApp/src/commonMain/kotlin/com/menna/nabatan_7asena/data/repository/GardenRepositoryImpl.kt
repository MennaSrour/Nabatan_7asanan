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
private data class GardenSnapshot(val version: Int = 1, val deviceId: String, val profiles: Map<String, GardenProgress> = emptyMap())

@OptIn(ExperimentalUuidApi::class)
class GardenRepositoryImpl(
    private val storage: GardenStorage = PlatformGardenStorage(),
    private val backup: suspend (String, String, GardenProgress) -> Unit
) : GardenRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private val syncMutex = Mutex()
    private var loadError: Exception? = null
    private val state = MutableStateFlow(readInitial())

    private fun readInitial(): GardenSnapshot = try {
        val snapshot = storage.read()?.let { json.decodeFromString<GardenSnapshot>(it) } ?: GardenSnapshot(deviceId = Uuid.random().toString())
        require(snapshot.version == 1 && snapshot.deviceId.isNotBlank())
        snapshot.profiles.values.forEach { it.validate() }
        snapshot
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        loadError = e
        GardenSnapshot(deviceId = Uuid.random().toString())
    }

    override fun observe(profileId: String): Flow<Resource<GardenProgress>> = flow {
        val failure = loadError
        if (failure != null) emit(Resource.Error(failure))
        else emitAll(state.map { Resource.Success(it.profiles[profileId] ?: GardenProgress()) }.distinctUntilChanged())
    }

    override suspend fun creditTasks(profileId: String, date: LocalDate, completedIds: Set<Int>) =
        update(profileId) { awardGardenTasks(it, date, completedIds) }

    override suspend fun water(profileId: String, date: LocalDate): GardenWatering {
        var applied = false
        val progress = update(profileId) {
            applied = it.waterDrops > 0
            waterGarden(it, date)
        }
        return GardenWatering(progress, applied)
    }

    private suspend fun update(profileId: String, transform: (GardenProgress) -> GardenProgress): GardenProgress = mutex.withLock {
        check(loadError == null) { "Garden storage could not be read" }
        require(profileId.isNotBlank())
        val current = state.value.profiles[profileId] ?: GardenProgress()
        val next = transform(current)
        next.validate()
        if (next != current) {
            val snapshot = state.value.copy(profiles = state.value.profiles + (profileId to next))
            storage.write(json.encodeToString(GardenSnapshot.serializer(), snapshot))
            state.value = snapshot
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
