package com.menna.nabatan_7asena.data.local.room

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerGuideRewardTest {
    @Test fun simultaneousClaimsAwardStarsOnlyOncePerDay() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = database.appDao()
            dao.insertUserStats(UserStatsEntity(userId = "child", totalStars = 48, currentStreak = 2))

            val results = List(3) { async { dao.awardPrayerGuideBonus("2026-11-21", "child") } }.awaitAll()
            assertEquals(1, results.count { it })
            assertEquals(53, dao.getUserStats().first()?.totalStars)
            assertEquals(2, dao.getUserStats().first()?.treeStage)
            assertEquals(2, dao.getUserStats().first()?.currentStreak)
            assertTrue(dao.awardPrayerGuideBonus("2026-11-22", "child"))
            assertEquals(58, dao.getUserStats().first()?.totalStars)
        } finally {
            database.close()
        }
    }
}
