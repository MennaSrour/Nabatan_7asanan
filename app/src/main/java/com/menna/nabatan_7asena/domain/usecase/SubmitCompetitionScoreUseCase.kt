package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.data.repository.QuizRepositoryImpl
import jakarta.inject.Inject

class SubmitCompetitionScoreUseCase @Inject constructor(
    private val repository: QuizRepositoryImpl
) {
    suspend operator fun invoke(score: Int) = repository.submitCompetitionScore(score)
}