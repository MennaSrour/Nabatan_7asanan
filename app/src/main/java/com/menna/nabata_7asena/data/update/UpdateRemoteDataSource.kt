package com.menna.nabata_7asena.data.update

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.menna.nabata_7asena.BuildConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRemoteDataSource @Inject constructor() {

    private val remoteConfig = Firebase.remoteConfig

    init {
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(settings)

        remoteConfig.setDefaultsAsync(
            mapOf(
                "latest_version_code" to BuildConfig.VERSION_CODE,
                "latest_version_name" to BuildConfig.VERSION_NAME,
                "force_update" to false,
                "update_message_ar" to "",
                "update_url" to "",
                "whats_new_ar" to ""
            )
        )
    }

    suspend fun fetchUpdate(): Map<String, Any> {
        remoteConfig.fetchAndActivate().await()

        return mapOf(
            "latest_version_code" to remoteConfig.getLong("latest_version_code"),
            "latest_version_name" to remoteConfig.getString("latest_version_name"),
            "force_update" to remoteConfig.getBoolean("force_update"),
            "update_message_ar" to remoteConfig.getString("update_message_ar"),
            "update_url" to remoteConfig.getString("update_url"),
            "whats_new_ar" to remoteConfig.getString("whats_new_ar")
        )
    }
}