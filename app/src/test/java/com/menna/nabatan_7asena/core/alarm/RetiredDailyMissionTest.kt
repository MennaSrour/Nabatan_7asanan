package com.menna.nabatan_7asena.core.alarm

import org.junit.Assert.*
import org.junit.Test

class RetiredDailyMissionTest {
    @Test fun blocksTheExactLegacyMissionNotification() {
        assertTrue(RetiredDailyMission.matches("تحدي اليوم يا بطل 💪"))
    }

    @Test fun doesNotBlockPrayerAzkarQuranOrMissingTitles() {
        listOf(null, "", "صلاة الفجر", "أذكار الصباح", "ورد القرآن", "تذكير جديد").forEach {
            assertFalse(RetiredDailyMission.matches(it))
        }
    }
}
