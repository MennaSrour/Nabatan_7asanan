package com.menna.nabatan_7asena.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/** Channels queue effects while no screen is STARTED; one route consumes each effect. */
@Composable
fun <Effect> CollectEffects(effects: Flow<Effect>, onEffect: suspend (Effect) -> Unit) {
    val owner = LocalLifecycleOwner.current
    val currentHandler by rememberUpdatedState(onEffect)
    LaunchedEffect(effects, owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effects.collect { currentHandler(it) }
        }
    }
}
