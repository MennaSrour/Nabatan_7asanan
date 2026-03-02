package com.menna.nabata_7asena.domain.entity

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val isForceUpdate: Boolean,
    val latestVersionName: String,
    val updateMessage: String,
    val whatsNew: String,
    val downloadUrl: String
)