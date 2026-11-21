package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

internal data class GardenFrame(val stage: Float, val phase: Float, val water: Float)

@Composable
internal fun GardenScene(stage: Int, profileId: String, wateringSequence: Long, description: String, modifier: Modifier = Modifier) {
    val growth = animateFloatAsState(stage.toFloat(), tween(1800), label = "gardenGrowth")
    val active = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value.isAtLeast(Lifecycle.State.STARTED)
    val phase = if (active) rememberInfiniteTransition(label = "gardenWind").animateFloat(0f, 1f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "gardenSky") else remember { mutableStateOf(0f) }
    val water = remember(profileId) { Animatable(0f) }
    var lastSequence by remember(profileId) { mutableLongStateOf(wateringSequence) }
    LaunchedEffect(profileId, wateringSequence) {
        if (wateringSequence <= lastSequence) return@LaunchedEffect
        lastSequence = wateringSequence
        water.snapTo(0f)
        water.animateTo(1f, tween(1600, easing = LinearEasing))
    }
    Canvas(modifier.semantics { contentDescription = description }) {
        val frame = GardenFrame(growth.value, phase.value, if (water.isRunning) water.value else 0f)
        withTransform({ scale(size.width / 1000f, size.height / 640f, Offset.Zero) }) {
            drawGardenLandscape(frame)
            drawGardenTree(frame)
            drawGardenEffects(frame)
        }
    }
}
