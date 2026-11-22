package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

/** Canvas-local coordinates: plot zero is the rightmost plot in the RTL world. */
internal fun gardenPlotAt(x: Float, canvasWidth: Float, plots: Int): Int {
    require(canvasWidth > 0 && plots > 0)
    return (plots - 1 - (x / (canvasWidth / plots)).toInt()).coerceIn(0, plots - 1)
}

internal fun visibleGardenPlots(scroll: Int, viewport: Int, plotPixels: Float, plots: Int): IntRange {
    require(plotPixels > 0 && plots > 0)
    val first = (scroll.coerceAtLeast(0) / plotPixels).toInt().coerceIn(0, plots - 1)
    val last = ((scroll.coerceAtLeast(0).toLong() + viewport.coerceAtLeast(0)) / plotPixels).toInt() + 1
    return first..last.coerceIn(first, plots - 1)
}
