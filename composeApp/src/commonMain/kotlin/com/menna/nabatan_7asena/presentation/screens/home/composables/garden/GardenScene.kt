package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.menna.nabatan_7asena.domain.entity.GardenSpecies

internal data class GardenFrame(val stage: Float, val phase: Float, val water: Float,
    val night: Boolean = false, val equipped: Set<String> = emptySet(), val species: GardenSpecies = GardenSpecies.APPLE)

@Composable
internal fun rememberGardenPhase(): State<Float> {
    val active = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value.isAtLeast(Lifecycle.State.STARTED)
    return if (active) rememberInfiniteTransition(label = "gardenWind").animateFloat(0f, 1f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "gardenSky") else remember { mutableStateOf(0f) }
}
