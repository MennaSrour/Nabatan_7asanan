package com.menna.nabatan_7asena.core.alarm

import android.app.PendingIntent
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test

class RetiredDailyMissionCleanupTest {
    @Test fun cancellingOldMissionLeavesPrayerPendingIntentIntact() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, UnifiedAlarmReceiver::class.java)
        val create = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val lookup = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        val mission = PendingIntent.getBroadcast(context, 4001, intent, create)
        val prayer = PendingIntent.getBroadcast(context, AlarmConstants.ID_FAJR, intent, create)
        try {
            RetiredDailyMission.cancel(context)
            assertNull(PendingIntent.getBroadcast(context, 4001, intent, lookup))
            assertNotNull(PendingIntent.getBroadcast(context, AlarmConstants.ID_FAJR, intent, lookup))
        } finally {
            mission.cancel()
            prayer.cancel()
        }
    }
}
