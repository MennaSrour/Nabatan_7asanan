package com.menna.nabata_7asena.data.update

import com.menna.nabata_7asena.BuildConfig
import com.menna.nabata_7asena.domain.entity.UpdateInfo
import com.menna.nabata_7asena.domain.repository.UpdateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRepositoryImpl @Inject constructor(
    private val remoteDataSource: UpdateRemoteDataSource
) : UpdateRepository {

    override suspend fun checkForUpdates(): UpdateInfo {

        val data = remoteDataSource.fetchUpdate()

        val latestVersionCode = (data["latest_version_code"] as Long).toInt()
        val currentVersionCode = BuildConfig.VERSION_CODE

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