@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, org.jetbrains.compose.resources.ExperimentalResourceApi::class)
package com.menna.nabatan_7asena.platform

import platform.Foundation.*
import com.menna.nabatan.shared.generated.resources.Res

actual class PlatformPreferences actual constructor(private val name: String) {
    private val defaults = NSUserDefaults.standardUserDefaults
    private fun key(key: String) = "$name.$key"
    actual fun getString(key: String, default: String?): String? = defaults.stringForKey(key(key)) ?: default
    actual fun getInt(key: String, default: Int): Int = if (defaults.objectForKey(key(key)) == null) default else defaults.integerForKey(key(key)).toInt()
    actual fun getBoolean(key: String, default: Boolean): Boolean = if (defaults.objectForKey(key(key)) == null) default else defaults.boolForKey(key(key))
    actual fun putString(key: String, value: String) { defaults.setObject(value, forKey = key(key)) }
    actual fun putInt(key: String, value: Int) { defaults.setInteger(value.toLong(), forKey = key(key)) }
    actual fun putBoolean(key: String, value: Boolean) { defaults.setBool(value, forKey = key(key)) }
    actual fun clear() {
        defaults.dictionaryRepresentation().keys.filterIsInstance<String>().filter { it.startsWith("$name.") }
            .forEach { defaults.removeObjectForKey(it) }
    }
}
actual fun readResourceText(path: String): String {
    val uri = NSURL.URLWithString(Res.getUri(path)) ?: error("Invalid resource URI")
    return NSString.create(contentsOfFile = uri.path ?: error("Resource path unavailable"), encoding = NSUTF8StringEncoding, error = null)?.toString()
        ?: error("Unable to read bundled resource: $path")
}
actual fun platformLog(level: String, tag: String, message: String) { println("$level/$tag: $message") }
actual object PlatformInfo {
    actual val versionCode: Int get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toIntOrNull() ?: 1
    actual val versionName: String get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "1.0"
    actual val supportsAppUpdates = false // Android's Play Store update contract does not apply to iOS.
}
actual fun openAppStore() = Unit

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher get() = kotlinx.coroutines.Dispatchers.Default
