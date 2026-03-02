package com.menna.nabata_7asena.domain.usecase

import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, gender: User.Gender): Resource<Unit> {
        if (name.isBlank()) return Resource.Error(IllegalArgumentException("Name cannot be empty"))
        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            gender = gender,
            totalStars = 0,
            currentStreak = 0,
            treeStage = 1,
            quranPartsFinished = 0,
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