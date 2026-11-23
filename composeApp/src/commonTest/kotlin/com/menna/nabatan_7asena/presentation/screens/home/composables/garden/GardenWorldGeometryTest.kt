package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import kotlin.test.Test
import kotlin.test.assertEquals

class GardenWorldGeometryTest {
    @Test fun fitsPhoneAndTabletWidths() {
        assertEquals(1, gardenGridColumns(235f, 8f))
        assertEquals(2, gardenGridColumns(320f, 8f))
        assertEquals(3, gardenGridColumns(450f, 14f))
        assertEquals(5, gardenGridColumns(800f, 14f))
        assertEquals(7, gardenGridColumns(1120f, 14f))
    }
}
