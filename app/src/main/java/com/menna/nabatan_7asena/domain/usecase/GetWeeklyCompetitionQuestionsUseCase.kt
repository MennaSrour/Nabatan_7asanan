package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.repository.QuizRepository
import javax.inject.Inject

class GetWeeklyCompetitionQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): List<QuizQuestion> = repository.getWeeklyCompetitionQuestions()
}
