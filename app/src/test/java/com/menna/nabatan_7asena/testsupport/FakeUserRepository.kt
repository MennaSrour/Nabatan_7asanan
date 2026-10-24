package com.menna.nabatan_7asena.testsupport

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepository : UserRepository {
    val user = MutableStateFlow(User("saved-user", "Test", User.Gender.GIRL, 50, 3, 2, avatarIndex = 1))
    val avatarWrites = mutableListOf<Int>()
    var saveAvatar: suspend (Int) -> Unit = { index -> user.value = user.value.copy(avatarIndex = index) }

    override fun getCurrentUser() = user
    override suspend fun updateProfileImage(userId: String, gender: User.Gender, avatarIndex: Int) {
        check(user.value.id == userId && user.value.gender == gender)
        avatarWrites += avatarIndex
        saveAvatar(avatarIndex)
    }
    override suspend fun registerUser(user: User) { this.user.value = user }
    override fun getLeaderboardResource() = flowOf<Resource<List<LeaderboardEntry>>>(Resource.Success(emptyList()))
    override suspend fun syncUserScore(user: User) = Unit
    override suspend fun updateUserName(newName: String) { user.value = user.value.copy(name = newName) }
    override suspend fun resetAllProgress() = Unit
    override suspend fun setNotificationsEnabled(enabled: Boolean) = Unit
    override suspend fun incrementQuranPart() = Resource.Success(Unit)
    override suspend fun decrementQuranPart() = Resource.Success(Unit)
    override suspend fun updateNotificationPreference(category: String, enabled: Boolean) = Unit
}
