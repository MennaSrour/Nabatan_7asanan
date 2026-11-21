package com.menna.nabatan_7asena.data.remote

import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto

interface OfficialQuizRemote {
    suspend fun getOfficialQuizAttempt(weekId: String, userId: String): QuizScoreDto?
    suspend fun reserveOfficialQuizAttempt(weekId: String, dto: QuizScoreDto): Pair<QuizScoreDto, Boolean>
    suspend fun finishOfficialQuizAttempt(weekId: String, userId: String, token: String, score: Int): QuizScoreDto
}
