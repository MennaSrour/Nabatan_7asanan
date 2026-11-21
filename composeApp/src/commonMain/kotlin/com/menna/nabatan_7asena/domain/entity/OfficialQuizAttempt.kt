package com.menna.nabatan_7asena.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class OfficialQuizAttempt(val weekId: String, val userId: String, val token: String,
    val score: Int = 0, val completed: Boolean = false, val synced: Boolean = true)
class OfficialAttemptAlreadyUsed(val attempt: OfficialQuizAttempt) : IllegalStateException("Official attempt already used")
