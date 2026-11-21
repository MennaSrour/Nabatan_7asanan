package com.menna.nabatan_7asena.data

import com.menna.nabatan_7asena.data.local.QuranLibraryStore
import com.menna.nabatan_7asena.data.local.QuranLibraryStorage
import kotlin.test.*

class QuranLibraryStoreTest {
    private class Memory : QuranLibraryStorage {
        var data: String? = null
        var failWrites = false
        override fun read() = data
        override fun write(value: String) { check(!failWrites); data = value }
    }

    @Test fun editsAndViewPreferenceSurviveRecreationInExactOrder() {
        val disk = Memory()
        val store = QuranLibraryStore(disk)
        val id = store.create("المفضلة")
        listOf(93, 89, 88).forEach { store.addSurah(id, it) }
        store.addSurah(id, 89)
        store.moveSurah(id, 2, 0)
        store.removeSurah(id, 89)
        store.rename(id, "صباحي")
        store.setMode("MUSHAF")
        val restored = QuranLibraryStore(disk).state.value
        assertEquals("MUSHAF", restored.viewMode)
        assertEquals("صباحي", restored.playlists.single().name)
        assertEquals(listOf(88, 93), restored.playlists.single().surahIds)
        store.delete(id)
        assertTrue(QuranLibraryStore(disk).state.value.playlists.isEmpty())
    }

    @Test fun failedWriteDoesNotPublishUnpersistedChanges() {
        val disk = Memory()
        val store = QuranLibraryStore(disk)
        val id = store.create("المفضلة")
        val before = store.state.value
        disk.failWrites = true
        assertFailsWith<IllegalStateException> { store.addSurah(id, 93) }
        assertEquals(before, store.state.value)
        assertEquals(before, QuranLibraryStore(disk).state.value)
    }

    @Test fun corruptStorageIsReportedWithoutOverwritingTheStoredPayload() {
        val disk = Memory().apply { data = "broken JSON" }
        val store = QuranLibraryStore(disk)
        assertTrue(store.loadFailed)
        assertTrue(store.state.value.playlists.isEmpty())
        assertEquals("broken JSON", disk.data)
    }

    @Test fun blankNamesAndInvalidSurahsAreRejected() {
        val store = QuranLibraryStore(Memory())
        assertFailsWith<IllegalArgumentException> { store.create("   ") }
        val id = store.create("المفضلة")
        assertFailsWith<IllegalArgumentException> { store.addSurah(id, 115) }
        assertFailsWith<IllegalArgumentException> { store.rename(id, "") }
        assertEquals("المفضلة", store.state.value.playlists.single().name)
    }
}
