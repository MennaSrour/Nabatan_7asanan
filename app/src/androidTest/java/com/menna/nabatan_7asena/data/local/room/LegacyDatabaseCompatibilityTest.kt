package com.menna.nabatan_7asena.data.local.room

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegacyDatabaseCompatibilityTest {
    @Test
    fun preKmpVersionFiveDatabaseRetainsProfileAndPreferences() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "legacy-kmp-compatibility-test.db"
        context.deleteDatabase(filename)
        val file = context.getDatabasePath(filename)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { database ->
            database.execSQL("CREATE TABLE IF NOT EXISTS `daily_records` (`date` TEXT NOT NULL, `itemId` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, PRIMARY KEY(`date`, `itemId`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `user_stats` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `gender` TEXT NOT NULL, `userId` TEXT NOT NULL, `avatarIndex` INTEGER NOT NULL, `totalStars` INTEGER NOT NULL, `treeStage` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `lastActiveDate` TEXT NOT NULL, `quranPartsFinished` INTEGER NOT NULL, `tasksCompletedTotal` INTEGER NOT NULL, `lastQuranPage` INTEGER NOT NULL, `prayerNotifications` INTEGER NOT NULL, `azkarNotifications` INTEGER NOT NULL, `quranNotifications` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `daily_plans` (`dayId` INTEGER NOT NULL, `challenge` TEXT NOT NULL, `wisdom` TEXT NOT NULL, `quranDhuhr` TEXT NOT NULL, `quranAsr` TEXT NOT NULL, `quranMaghrib` TEXT NOT NULL, `riddleQuestion` TEXT NOT NULL, `riddleAnswer` TEXT NOT NULL, `riddleOptions` TEXT NOT NULL, PRIMARY KEY(`dayId`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `extra_tasks` (`id` INTEGER NOT NULL, `date` TEXT NOT NULL, `title` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
            database.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1cd15c483783e59fc97c135613762fbd')")
            database.execSQL("INSERT INTO user_stats VALUES (1, 'نور', 'GIRL', 'existing-user', 4, 123, 2, 7, '20-11-2026', 3, 80, 90, 0, 1, 0)")
            database.version = 5
        }
        val database = Room.databaseBuilder(context, AppDatabase::class.java, filename)
            .addMigrations(MIGRATION_4_5, ANDROID_MIGRATION_5_6).build()
        try {
            val user = database.appDao().getUserStats().first()!!
            assertEquals("existing-user", user.userId)
            assertEquals("نور", user.name)
            assertEquals("GIRL", user.gender)
            assertEquals(4, user.avatarIndex)
            assertEquals(123, user.totalStars)
            assertEquals(90, user.lastQuranPage)
            assertEquals(false, user.prayerNotifications)
            assertEquals(true, user.azkarNotifications)
            assertEquals(false, user.quranNotifications)
            val dao = database.appDao()
            val reward = DailyRewardEntity(user.userId, "2026-11-21", "wheel", "STARS_20", 20, false)
            assertEquals(true, dao.claimReward(reward))
            assertEquals(false, dao.claimReward(reward.copy(stars = 50)))
            assertEquals(user.copy(totalStars = 143), dao.getUserStats().first())
            assertEquals(listOf(reward), dao.pendingRewards(user.userId))
        } finally {
            database.close()
            context.deleteDatabase(filename)
        }
    }

    @Test fun concurrentGamePayoutsCreditOncePerGameAndDay() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java
        ).build()
        try {
            val dao = database.appDao()
            dao.insertUserStats(UserStatsEntity(userId = "saved", totalStars = 100))
            val memory = DailyRewardEntity("saved", "2026-11-21", "MEMORY", "", 10, false)
            val attempts = List(8) { async { dao.claimReward(memory) } }.awaitAll()
            assertEquals(1, attempts.count { it })
            assertTrue(dao.claimReward(memory.copy(kind = "VERSE")))
            assertTrue(dao.claimReward(memory.copy(day = "2026-11-22")))
            assertFalse(dao.claimReward(memory.copy(day = "2026-11-20")))
            assertEquals(130, dao.getUserStats().first()?.totalStars)
        } finally {
            database.close()
        }
    }

    @Test fun reservedWheelCreditsStarsOnlyWhenTheClaimSettles() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java
        ).build()
        try {
            val dao = database.appDao()
            dao.insertUserStats(UserStatsEntity(userId = "saved", totalStars = 100))
            val reservation = DailyRewardEntity(
                "saved", "2026-11-21", "wheel", "STARS_50", 0,
                synced = false, settled = false, claimId = "original"
            )
            assertTrue(dao.claimReward(reservation))
            assertFalse(dao.claimReward(reservation.copy(claimId = "second")))
            assertEquals(100, dao.getUserStats().first()?.totalStars)
            val settled = reservation.copy(stars = 50, settled = true)
            assertTrue(dao.settleReward(settled))
            assertFalse(dao.settleReward(settled))
            assertEquals(150, dao.getUserStats().first()?.totalStars)
            assertEquals(listOf(settled), dao.pendingRewards("saved"))
        } finally {
            database.close()
        }
    }
}
