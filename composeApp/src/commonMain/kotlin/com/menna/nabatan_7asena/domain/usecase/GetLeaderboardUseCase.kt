package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetLeaderboardUseCase constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<LeaderboardEntry>>> {
        return repository.getLeaderboardResource()
    }
}