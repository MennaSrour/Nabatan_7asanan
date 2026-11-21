package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository

class GetCurrentUserRankUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User, leaderboard: List<LeaderboardEntry>): Resource<Long> {
        val index = leaderboard.indexOfFirst { it.userId == user.id }
        return if (index >= 0) Resource.Success(index + 1L)
        else repository.getUserRank(user.totalStars)
    }
}
