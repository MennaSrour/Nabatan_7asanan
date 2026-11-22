package com.menna.nabatan_7asena.core.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.menna.nabatan_7asena.data.local.PrayerSettingsStore
import com.menna.nabatan_7asena.platform.AndroidScheduling

/** Refresh day/timezone-dependent alarms after system clock changes or reboot. */
class PrayerEnvironmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in setOf(Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_DATE_CHANGED, Intent.ACTION_BOOT_COMPLETED)) return
        if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            PrayerSettingsStore.state.value.savedGps?.let {
                PrayerSettingsStore.saveGps(it.copy(timeZoneId = java.util.TimeZone.getDefault().id))
            }
        }
        AndroidScheduling().enableDailyAutoScheduling()
    }
}
