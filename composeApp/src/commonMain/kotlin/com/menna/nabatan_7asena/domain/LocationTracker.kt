package com.menna.nabatan_7asena.domain

import com.menna.nabatan_7asena.domain.entity.UserLocation

interface LocationTracker {
    suspend fun getCurrentLocation(): UserLocation? 
}