package com.menna.nabatan_7asena.domain

import com.menna.nabatan_7asena.domain.entity.UserLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LocationTracker {
    suspend fun getCurrentLocation(): UserLocation?
    fun foregroundLocations(): Flow<UserLocation> = flow {
        while (true) {
            getCurrentLocation()?.let { emit(it) }
            delay(5 * 60_000L)
        }
    }
}
