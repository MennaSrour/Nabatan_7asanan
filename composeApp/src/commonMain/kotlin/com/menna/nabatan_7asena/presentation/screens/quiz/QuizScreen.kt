package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.quiz.composables.QuizContent

@Composable
fun QuizScreen(viewModel: QuizViewModel = platformQuizViewModel()) {
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner, viewModel) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) { viewModel.refreshAvailability(); delay(30_000) }
        }
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is QuizEffect.AnswerFeedback -> haptic.performHapticFeedback(
                if (effect.isCorrect) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress
            )
        }
    }
    QuizContent(state, viewModel)
}
