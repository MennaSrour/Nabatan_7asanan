package com.menna.nabatan_7asena.data.local.room

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
            .addMigrations(MIGRATION_4_5).build()
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
        } finally {
            database.close()
            context.deleteDatabase(filename)
        }
    }
}
