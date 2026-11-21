package com.menna.nabatan_7asena.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Cancel old alarms immediately after an app update, even before the next manual launch. */
class RetiredDailyMissionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) RetiredDailyMission.cancel(context)
    }
}
