package com.menna.nabatan_7asena.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class KidsShortDtoTest {
    private val clip = KidsShortDto(
        title = "بسم الله",
        videoUrl = "https://media.example.org/clip.mp4",
        thumbnailUrl = "https://media.example.org/clip.jpg",
        category = "آداب الطعام",
        order = 3,
        isActive = true
    )

    @Test
    fun activeClipsUseDocumentIdWhenFieldIsMissing() {
        val mapped = assertNotNull(clip.toDomain("clip-1"))
        assertEquals("clip-1", mapped.id)
        assertEquals(3L, mapped.order)
        assertEquals("clip-2", clip.copy(id = "clip-2").toDomain("clip-1")?.id)
    }

    @Test
    fun inactiveOrInvalidClipsCannotEnterTheFeed() {
        assertNull(clip.copy(isActive = false).toDomain("clip-1"))
        assertNull(clip.copy(title = " ").toDomain("clip-1"))
        assertNull(clip.copy(videoUrl = "file:///clip.mp4").toDomain("clip-1"))
        assertNull(clip.copy(thumbnailUrl = "//media.example.org/clip.jpg").toDomain("clip-1"))
    }
}
