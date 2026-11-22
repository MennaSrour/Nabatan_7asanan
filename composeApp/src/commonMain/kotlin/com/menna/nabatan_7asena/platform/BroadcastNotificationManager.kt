package com.menna.nabatan_7asena.platform

internal const val BROADCAST_TOPIC = "all_users"

expect object BroadcastNotificationManager {
    fun initialize()
}
