package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuizRepository

class IsCompetitionDayUseCase constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Boolean = repository.isCompetitionDayToday()
}
