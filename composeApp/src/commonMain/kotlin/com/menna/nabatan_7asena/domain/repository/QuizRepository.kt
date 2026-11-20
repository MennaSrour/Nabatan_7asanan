package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.entity.QuizWinner

interface QuizRepository {
    suspend fun getWeeklyStudyQuestions(): List<QuizQuestion>
    suspend fun getWeeklyCompetitionQuestions(): List<QuizQuestion>
    fun isCompetitionDayToday(): Boolean
    suspend fun submitCompetitionScore(score: Int)
    suspend fun getRecentWeekWinners(): List<QuizWinner>
}