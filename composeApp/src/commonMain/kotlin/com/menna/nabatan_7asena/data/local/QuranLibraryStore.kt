package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.domain.entity.QuranPlaylist
import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface QuranLibraryStorage {
    fun read(): String?
    fun write(value: String)
}

private class PlatformQuranLibraryStorage : QuranLibraryStorage {
    private val preferences = PlatformPreferences("quran_library")
    override fun read() = preferences.getString("library_v1")
    override fun write(value: String) = preferences.putString("library_v1", value)
}

@Serializable
data class QuranLibrarySnapshot(val viewMode: String = "CARDS", val playlists: List<QuranPlaylist> = emptyList())

/** One JSON snapshot keeps playlist order and reader preferences consistent on both hosts. */
@OptIn(ExperimentalUuidApi::class)
class QuranLibraryStore(private val storage: QuranLibraryStorage = PlatformQuranLibraryStorage()) {
    private val json = Json { ignoreUnknownKeys = true }
    var loadFailed: Boolean = false
        private set
    private val _state = MutableStateFlow(readInitial())
    private fun readInitial(): QuranLibrarySnapshot = try {
        val saved = storage.read()?.let { json.decodeFromString<QuranLibrarySnapshot>(it) } ?: QuranLibrarySnapshot()
        require(saved.playlists.map { it.id }.distinct().size == saved.playlists.size)
        require(saved.playlists.all { it.id.isNotBlank() && it.name.isNotBlank() &&
            it.surahIds.all { id -> id in 1..114 } && it.surahIds.distinct() == it.surahIds })
        saved
    } catch (cancelled: kotlinx.coroutines.CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        loadFailed = true
        QuranLibrarySnapshot()
    }
    val state = _state.asStateFlow()

    fun setMode(mode: String) = save(_state.value.copy(viewMode = mode))
    fun create(name: String): String {
        require(name.isNotBlank())
        val id = Uuid.random().toString()
        save(_state.value.copy(playlists = _state.value.playlists + QuranPlaylist(id, name.trim())))
        return id
    }
    fun rename(id: String, name: String) {
        require(name.isNotBlank())
        update(id) { it.copy(name = name.trim()) }
    }
    fun delete(id: String) = save(_state.value.copy(playlists = _state.value.playlists.filterNot { it.id == id }))
    fun addSurah(id: String, surah: Int) {
        require(surah in 1..114)
        update(id) { if (surah in it.surahIds) it else it.copy(surahIds = it.surahIds + surah) }
    }
    fun removeSurah(id: String, surah: Int) = update(id) { it.copy(surahIds = it.surahIds - surah) }
    fun moveSurah(id: String, from: Int, to: Int) = update(id) {
        if (from !in it.surahIds.indices || to !in it.surahIds.indices) it
        else it.copy(surahIds = it.surahIds.toMutableList().apply { add(to, removeAt(from)) })
    }
    private fun update(id: String, transform: (QuranPlaylist) -> QuranPlaylist) =
        save(_state.value.copy(playlists = _state.value.playlists.map { if (it.id == id) transform(it) else it }))

    private fun save(next: QuranLibrarySnapshot) {
        storage.write(json.encodeToString(QuranLibrarySnapshot.serializer(), next))
        _state.value = next // Failed persistence never publishes a misleading success snapshot.
    }
}
