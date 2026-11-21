package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.entity.OfficialQuizAttempt

interface OfficialQuizRepository {
    suspend fun status(): OfficialQuizAttempt?
    suspend fun begin(): OfficialQuizAttempt
    suspend fun finish(attempt: OfficialQuizAttempt, score: Int): OfficialQuizAttempt
    suspend fun syncPending()
}
