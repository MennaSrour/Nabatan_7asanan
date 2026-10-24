package com.menna.nabatan_7asena.data.local.room

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProfileImagePersistenceTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java
        ).build()
        dao = database.appDao()
    }

    @After fun tearDown() { database.close() }

    @Test fun avatarUpdatePreservesEveryOtherProfileColumn() = runBlocking {
        val original = UserStatsEntity(userId = "saved", gender = "GIRL", name = "Test",
            totalStars = 123, currentStreak = 7, lastQuranPage = 80, prayerNotifications = false)
        dao.insertUserStats(original)
        assertEquals(1, dao.updateProfileImage("saved", "GIRL", 4))
        assertEquals(original.copy(avatarIndex = 4), dao.getUserStats().first())
    }

    @Test fun staleUserOrGenderCannotChangeTheCurrentAvatar() = runBlocking {
        val original = UserStatsEntity(userId = "saved", gender = "GIRL", avatarIndex = 2)
        dao.insertUserStats(original)
        assertEquals(0, dao.updateProfileImage("other", "GIRL", 4))
        assertEquals(0, dao.updateProfileImage("saved", "BOY", 4))
        assertEquals(original, dao.getUserStats().first())
    }
}
