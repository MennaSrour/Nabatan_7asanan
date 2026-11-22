package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import kotlin.test.*

class GardenWorldGeometryTest {
    @Test fun firstPlotIsOnTheRightAndHitTestsStayCorrectAcrossTheWholeWorld() {
        assertEquals(0, gardenPlotAt(999f, 1000f, 4))
        assertEquals(1, gardenPlotAt(625f, 1000f, 4))
        assertEquals(3, gardenPlotAt(1f, 1000f, 4))
        assertEquals(0, gardenPlotAt(1100f, 1000f, 4))
        assertEquals(3, gardenPlotAt(-10f, 1000f, 4))
    }
    @Test fun viewportCullingIncludesNearbyPlotsWithoutDrawingTheWholeExpandedWorld() {
        assertEquals(0..2, visibleGardenPlots(0, 360, 260f, 16))
        assertEquals(2..4, visibleGardenPlots(520, 360, 260f, 16))
        assertEquals(14..15, visibleGardenPlots(3800, 360, 260f, 16))
        assertEquals(3..3, visibleGardenPlots(9999, 360, 260f, 4))
    }
}
