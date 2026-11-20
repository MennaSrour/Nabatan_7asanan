package com.menna.nabatan_7asena.data.update

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import com.menna.nabatan_7asena.platform.PlatformInfo

class UpdateRemoteDataSource {
    suspend fun fetchUpdate(): Map<String, Any> {
        val config = Firebase.remoteConfig
        config.setDefaults("latest_version_code" to PlatformInfo.versionCode,
            "latest_version_name" to PlatformInfo.versionName, "force_update" to false,
            "update_message_ar" to "", "update_url" to "", "whats_new_ar" to "")
        config.fetchAndActivate()
        return mapOf("latest_version_code" to config.getValue("latest_version_code").asLong(),
            "latest_version_name" to config.getValue("latest_version_name").asString(),
            "force_update" to config.getValue("force_update").asBoolean(),
            "update_message_ar" to config.getValue("update_message_ar").asString(),
            "update_url" to config.getValue("update_url").asString(),
            "whats_new_ar" to config.getValue("whats_new_ar").asString())
    }
}
