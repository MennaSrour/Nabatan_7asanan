package com.menna.nabata_7asena.domain

import com.menna.nabata_7asena.domain.entity.UserLocation

interface LocationTracker {
    suspend fun getCurrentLocation(): UserLocation? 
}