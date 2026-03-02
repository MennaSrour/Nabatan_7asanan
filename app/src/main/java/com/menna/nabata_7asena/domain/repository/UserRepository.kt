package com.menna.nabata_7asena.domain.repository

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.LeaderboardEntry
import com.menna.nabata_7asena.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User>

    suspend fun registerUser(user: User)

    fun getLeaderboardResource(): Flow<Resource<List<LeaderboardEntry>>>

    suspend fun syncUserScore(user: User)
    suspend fun updateUserName(newName: String)
    suspend fun resetAllProgress()
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun incrementQuranPart(): Resource<Unit>
    suspend fun decrementQuranPart(): Resource<Unit>
    suspend fun updateNotificationPreference(category: String, enabled: Boolean)
}