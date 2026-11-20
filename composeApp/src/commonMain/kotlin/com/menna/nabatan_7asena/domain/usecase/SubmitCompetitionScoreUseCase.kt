package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuizRepository

class SubmitCompetitionScoreUseCase constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(score: Int) = repository.submitCompetitionScore(score)
}