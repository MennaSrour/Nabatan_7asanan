package com.menna.nabatan_7asena.data.local.room

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RamadanCalendarPersistenceTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java
        ).build()
        dao = database.appDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun concurrentCompletionOnlyAwardsOnceAndDoesNotCountAsADailyTask() = runBlocking {
        dao.insertUserStats(UserStatsEntity(userId = "saved", totalStars = 46))
        val first = async { dao.completeRamadanDay("ramadan-1448", "saved", 3, awardStars = true) }
        val second = async { dao.completeRamadanDay("ramadan-1448", "saved", 3, awardStars = true) }
        first.await()
        second.await()
        assertEquals(listOf(3), dao.observeRamadanDays("saved", "ramadan-1448").first())
        assertEquals(0, dao.observeCompletedDays(3).first())
        val stats = requireNotNull(dao.readUserStats())
        assertEquals(51, stats.totalStars)
        assertEquals(2, stats.treeStage)
        assertEquals(0, stats.tasksCompletedTotal)
    }

    @Test
    fun previewProgressStaysSeparateFromRealStarsAndBackups() = runBlocking {
        dao.insertUserStats(UserStatsEntity(userId = "saved"))
        dao.completeRamadanDay("ramadan-preview", "saved", 30, awardStars = false)
        assertEquals(listOf(30), dao.observeRamadanDays("saved", "ramadan-preview").first())
        assertTrue(dao.observeRamadanDays("saved", "ramadan-1448").first().isEmpty())
        assertTrue(dao.observeRamadanRecords("saved").first().isEmpty())
        assertEquals(0, dao.readUserStats()?.totalStars)
        dao.replaceProfile(UserStatsEntity(userId = "new"))
        assertTrue(dao.observeRamadanDays("new", "ramadan-preview").first().isEmpty())
    }
}
