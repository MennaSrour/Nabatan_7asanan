package com.menna.nabata_7asena.domain.repository

import com.menna.nabata_7asena.domain.entity.UpdateInfo

interface UpdateRepository {
    suspend fun checkForUpdates(): UpdateInfo
}