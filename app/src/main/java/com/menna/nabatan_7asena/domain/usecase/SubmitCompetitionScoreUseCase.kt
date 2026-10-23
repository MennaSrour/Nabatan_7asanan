package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuizRepository
import jakarta.inject.Inject

class SubmitCompetitionScoreUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(score: Int) = repository.submitCompetitionScore(score)
}