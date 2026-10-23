package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.repository.QuizRepository
import javax.inject.Inject

class IsCompetitionDayUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Boolean = repository.isCompetitionDayToday()
}
