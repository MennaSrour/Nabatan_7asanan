package com.menna.nabatan_7asena.data.update

import com.menna.nabatan_7asena.platform.PlatformInfo
import com.menna.nabatan_7asena.domain.entity.UpdateInfo
import com.menna.nabatan_7asena.domain.repository.UpdateRepository

class UpdateRepositoryImpl constructor(
    private val remoteDataSource: UpdateRemoteDataSource
) : UpdateRepository {

    override suspend fun checkForUpdates(): UpdateInfo {

        if (!PlatformInfo.supportsAppUpdates) return UpdateInfo(false, false, PlatformInfo.versionName, "", "", "")
        val data = remoteDataSource.fetchUpdate()

        val latestVersionCode = (data["latest_version_code"] as Long).toInt()
        val currentVersionCode = PlatformInfo.versionCode

        val isAvailable = latestVersionCode > currentVersionCode

        return UpdateInfo(
            isUpdateAvailable = isAvailable,
            isForceUpdate = (data["force_update"] as Boolean) && isAvailable,
            latestVersionName = data["latest_version_name"] as String,
            updateMessage = data["update_message_ar"] as String,
            whatsNew = data["whats_new_ar"] as String,
            downloadUrl = data["update_url"] as String
        )
    }
}