package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class UpdateProfileImageUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(userId: String, gender: User.Gender, avatarIndex: Int): Resource<Unit> {
        return try {
            require(userId.isNotBlank()) { "A saved user is required" }
            require(avatarIndex >= 0) { "Invalid avatar index" }
            repository.updateProfileImage(userId, gender, avatarIndex)
            Resource.Success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Resource.Error(exception)
        }
    }
}
