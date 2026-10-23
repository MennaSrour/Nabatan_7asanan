package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.domain.repository.QuizRepository
import javax.inject.Inject

class GetRecentQuizWinnersUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): List<QuizWinner> = repository.getRecentWeekWinners()
}