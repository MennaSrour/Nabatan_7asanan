package com.menna.nabatan_7asena.platform

expect class PlatformPreferences(name: String) {
    fun getString(key: String, default: String? = null): String?
    fun getInt(key: String, default: Int = 0): Int
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
    fun putBoolean(key: String, value: Boolean)
    fun clear()
}
expect fun readResourceText(path: String): String
expect fun platformLog(level: String, tag: String, message: String)
expect object PlatformInfo {
    val versionCode: Int
    val versionName: String
    val supportsAppUpdates: Boolean
}
interface PermissionPlatform {
    val supportsAndroidPermissions: Boolean
    fun locationGranted(): Boolean
    fun notificationsGranted(): Boolean
    fun exactAlarmsGranted(): Boolean = true
    suspend fun refresh() = Unit
    fun permissionSkipped(permission: AppPermission) = Unit
    fun batteryExempt(): Boolean
}
interface SchedulingPlatform {
    fun enableDailyAutoScheduling(replace: Boolean = false)
    fun scheduleImmediately()
    fun cancelDailyAutoScheduling()
}
object Log {
    fun d(tag: String, message: String) = platformLog("DEBUG", tag, message)
    fun w(tag: String, message: String) = platformLog("WARN", tag, message)
    fun e(tag: String, message: String, error: Throwable? = null) = platformLog("ERROR", tag, message + (error?.let { "\n" + it.stackTraceToString() } ?: ""))
}

expect val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
fun toRadians(degrees: Double): Double = degrees * kotlin.math.PI / 180.0
