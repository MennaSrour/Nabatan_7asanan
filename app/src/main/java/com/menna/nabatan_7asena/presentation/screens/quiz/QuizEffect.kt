package com.menna.nabatan_7asena.presentation.screens.quiz

sealed interface QuizEffect {
    data class AnswerFeedback(val isCorrect: Boolean) : QuizEffect
}
