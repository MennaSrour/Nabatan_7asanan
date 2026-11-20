package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.entity.UpdateInfo

interface UpdateRepository {
    suspend fun checkForUpdates(): UpdateInfo
}