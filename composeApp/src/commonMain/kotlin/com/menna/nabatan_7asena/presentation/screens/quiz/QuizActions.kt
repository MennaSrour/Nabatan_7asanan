package com.menna.nabatan_7asena.presentation.screens.quiz

interface QuizActions {
    fun startStudyMode()
    fun startCompetitionMode()
    fun selectAnswer(choiceIndex: Int)
    fun goToNextCard()
    fun exitQuiz()
}
