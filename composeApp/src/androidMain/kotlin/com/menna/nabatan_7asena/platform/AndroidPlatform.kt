package com.menna.nabatan_7asena.platform

import android.content.Context
import android.content.Intent
import android.content.ActivityNotFoundException
import android.media.MediaPlayer
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.menna.nabatan_7asena.resources.ResourceCatalog
import com.menna.nabatan_7asena.core.alarm.RetiredDailyMission
import com.menna.nabatan.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

object AndroidPlatform {
    lateinit var context: Context
        private set
    fun initialize(context: Context) {
        this.context = context.applicationContext
        RetiredDailyMission.cancel(this.context)
    }
}
actual class PlatformPreferences actual constructor(name: String) {
    private val prefs = AndroidPlatform.context.getSharedPreferences(name, Context.MODE_PRIVATE)
    actual fun getString(key: String, default: String?): String? = prefs.getString(key, default)
    actual fun getInt(key: String, default: Int) = prefs.getInt(key, default)
    actual fun getBoolean(key: String, default: Boolean) = prefs.getBoolean(key, default)
    actual fun putString(key: String, value: String) { prefs.edit().putString(key, value).apply() }
    actual fun putInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }
    actual fun putBoolean(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }
    actual fun clear() { prefs.edit().clear().apply() }
}
@OptIn(ExperimentalResourceApi::class)
actual fun readResourceText(path: String): String {
    val asset = Res.getUri(path).removePrefix("file:///android_asset/")
    return AndroidPlatform.context.assets.open(asset).bufferedReader().use { it.readText() }
}
actual fun platformLog(level: String, tag: String, message: String) {
    android.util.Log.println(when (level) { "ERROR" -> android.util.Log.ERROR; "WARN" -> android.util.Log.WARN; else -> android.util.Log.DEBUG }, tag, message)
}
actual object PlatformInfo {
    private val info get() = AndroidPlatform.context.packageManager.getPackageInfo(AndroidPlatform.context.packageName, 0)
    actual val versionCode: Int get() = if (Build.VERSION.SDK_INT >= 28) info.longVersionCode.toInt() else info.versionCode
    actual val versionName: String get() = info.versionName.orEmpty()
    actual val supportsAppUpdates = true
}
actual fun openAppStore() {
    val context = AndroidPlatform.context
    try { context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=com.menna.nabatan_7asena")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    catch (_: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.menna.nabatan_7asena")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
@OptIn(ExperimentalResourceApi::class)
fun createResourcePlayer(context: Context, resource: Int): MediaPlayer? {
    val path = ResourceCatalog.rawPaths[resource] ?: return null
    val asset = Res.getUri(path).removePrefix("file:///android_asset/")
    return context.assets.openFd(asset).use { descriptor ->
        MediaPlayer().apply {
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            prepare()
        }
    }
}
class AndroidPermissions : PermissionPlatform {
    override val supportsAndroidPermissions = true
    private val context get() = AndroidPlatform.context
    override suspend fun refresh() {
        val current = "${locationGranted()}|${notificationsGranted()}|${exactAlarmsGranted()}|${batteryExempt()}"
        val prefs = PlatformPreferences("permission_requests")
        if (prefs.getString("status", null) != current) {
            prefs.putString("status", current)
            AndroidScheduling().scheduleImmediately()
        }
    }
    override fun locationGranted() = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    override fun notificationsGranted() = androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
    override fun exactAlarmsGranted() = Build.VERSION.SDK_INT < 31 || context.getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()
    override fun batteryExempt() = context.getSystemService(PowerManager::class.java)?.isIgnoringBatteryOptimizations(context.packageName) == true
}

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher get() = kotlinx.coroutines.Dispatchers.IO
