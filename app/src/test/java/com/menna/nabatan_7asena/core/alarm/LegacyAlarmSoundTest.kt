package com.menna.nabatan_7asena.core.alarm

import com.menna.nabatan_7asena.platform.alarmSoundResource
import com.menna.nabatan_7asena.resources.AppResources as R
import org.junit.Assert.assertEquals
import org.junit.Test

class LegacyAlarmSoundTest {
    @Test
    fun pendingPrayerUsesContentRegardlessOfOldReleaseResourceNumber() {
        for (oldId in listOf(0x7f110010, 0x7f110011)) {
            assertEquals(R.raw.fajr, alarmSoundResource(oldId, "الصلاة", "حان الآن موعد صلاة الفجر 🕌", "home"))
        }
    }

    @Test
    fun pendingQuranAndZekrRetainTheirOwnSounds() {
        assertEquals(R.raw.werd, alarmSoundResource(0x7f110040, "ورد القرآن 📖", "وردك اليومي جاهز", "quran"))
        assertEquals(R.raw.subhan_allah, alarmSoundResource(0x7f11003b, "ذكر الله", "سبحان الله", "sebha"))
        assertEquals(R.raw.yala_zekr, alarmSoundResource(0x7f110042, "أذكار الصباح", "أصبحنا وأصبح الملك لله", "sebha"))
    }

    @Test
    fun newStableIdsDoNotDependOnNotificationWording() {
        assertEquals(R.raw.zuhr, alarmSoundResource(R.raw.zuhr, "New title", "New message", null))
    }
}
