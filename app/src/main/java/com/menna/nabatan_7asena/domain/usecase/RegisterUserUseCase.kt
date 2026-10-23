package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, gender: User.Gender, avatarIndex: Int): Resource<Unit> {
        if (name.isBlank()) return Resource.Error(IllegalArgumentException("Name cannot be empty"))
        val newUser = User(
            id = Uuid.random().toString(),
            name = name,
            gender = gender,
            totalStars = 0,
            currentStreak = 0,
            treeStage = 1,
            quranPartsFinished = 0,
            avatarIndex = avatarIndex,
            prayerNotifications = true,
            azkarNotifications = true,
            quranNotifications = true
        )
        return try {
            repository.registerUser(newUser)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}