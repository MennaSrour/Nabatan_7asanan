package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.quiz.composables.QuizContent

@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel()) {
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
