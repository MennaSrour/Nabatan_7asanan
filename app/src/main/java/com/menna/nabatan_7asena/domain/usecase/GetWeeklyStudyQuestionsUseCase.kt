package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.repository.QuizRepository // Import interface domain
import com.menna.nabatan_7asena.domain.util.QuizWeek
import javax.inject.Inject

class GetWeeklyStudyQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): List<QuizQuestion> {
        val questions = repository.getWeeklyStudyQuestions()
        return QuizWeek.shuffledForSession(questions)
    }
}



