package com.menna.nabatan_7asena.presentation.screens.quiz

interface QuizActions {
    fun retryOfficialStatus()
    fun startStudyMode()
    fun startCompetitionMode()
    fun selectAnswer(choiceIndex: Int)
    fun goToNextCard()
    fun exitQuiz()
}
