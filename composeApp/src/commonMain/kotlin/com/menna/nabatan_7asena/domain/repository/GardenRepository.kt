package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.GardenProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GardenRepository {
    fun observe(profileId: String): Flow<Resource<GardenProgress>>
    suspend fun creditTasks(profileId: String, date: LocalDate, completedIds: Set<Int>): GardenProgress
    suspend fun water(profileId: String, date: LocalDate): com.menna.nabatan_7asena.domain.entity.GardenWatering
    suspend fun sync(profileId: String): Boolean
}
