package com.menna.nabatan_7asena.data.repository

import android.util.Log
import com.menna.nabatan_7asena.data.local.QuranBookmarkStore
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.DailyRecordEntity
import com.menna.nabatan_7asena.data.local.room.ExtraTaskEntity
import com.menna.nabatan_7asena.data.local.room.UserStatsEntity
import com.menna.nabatan_7asena.data.mappers.toDomain
import com.menna.nabatan_7asena.data.mappers.toEntity
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.domain.GetPrayerTimesUseCase
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.domain.entity.Activity
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.ExtraTasks
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.domain.repository.DailyActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

class DailyActivityRepositoryImpl @Inject constructor(
    private val appDao: AppDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val quranBookmarkStore: QuranBookmarkStore
) : DailyActivityRepository {

    private val dailyPlanCache = mutableMapOf<Int, DailyContent>()

    override fun getDailyActivities(
        date: String,
        city: String,
        country: String
    ): Flow<List<Activity>> {
        val recordsFlow = appDao.getRecordsForDate(date)
        val extraTasksFlow = appDao.getExtraTasksForDate(date)

         val dailyPlanFlow = flow {
            val dayId = getDayOfYear()
            emit(null)
             val plan = kotlinx.coroutines.withTimeoutOrNull(5000) {
                fetchDailyPlan(dayId)
            }
            if (plan != null) {
                emit(plan)
            }
        }

        return combine(recordsFlow, dailyPlanFlow, extraTasksFlow) { records, plan, extraTasks ->
            val statusMap = records.associate { it.itemId to it.isCompleted }
            val prayers = try {
                getPrayerTimesUseCase(city, country, date)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch prayer times for city=$city, country=$country, date=$date", e)
                null
            }
            val activities = mutableListOf<Activity>()

            if (prayers != null) {
                activities.add(Activity.Prayer(100, statusMap[100] ?: false, "الفجر", prayers.fajr))
                activities.add(
                    Activity.Prayer(
                        101,
                        statusMap[101] ?: false,
                        "الظهر",
                        prayers.dhuhr
                    )
                )
                activities.add(Activity.Prayer(102, statusMap[102] ?: false, "العصر", prayers.asr))
                activities.add(
                    Activity.Prayer(
                        103,
                        statusMap[103] ?: false,
                        "المغرب",
                        prayers.maghrib
                    )
                )
                activities.add(
                    Activity.Prayer(
                        104,
                        statusMap[104] ?: false,
                        "العشاء",
                        prayers.isha
                    )
                )
            }
            activities.add(
                Activity.Task(
                    id = 400,
                    isCompleted = statusMap[400] ?: false,
                    title = "ذكر الله 📿",
                    category = TaskCategory.AZKAR
                )
            )

            activities.add(
                Activity.Task(
                    id = 300,
                    isCompleted = statusMap[300] ?: false,
                    title = buildQuranWerdTitle(),
                    category = TaskCategory.QURAN
                )
            )

            if (plan != null) {
                activities.add(
                    Activity.Task(
                        200,
                        statusMap[200] ?: false,
                        plan.challengeTitle,
                        TaskCategory.CHALLENGE
                    )
                )
            } else {
                Log.w(TAG, "No daily plan available for today, Challenge task will not show")
            }

            extraTasks.forEach { extra ->
                activities.add(
                    Activity.Task(
                        extra.id,
                        statusMap[extra.id] ?: false,
                        extra.title,
                        TaskCategory.EXTRA
                    )
                )
            }

            activities.sortedBy { it.isCompleted }
        }
    }

    override suspend fun getTodayRiddle(): DailyContent? {
        val dayId = getDayOfYear()
        return fetchDailyPlan(dayId)
    }

    override suspend fun getPlanForDay(dayId: Int): DailyContent? {
        return fetchDailyPlan(dayId)
    }

    private suspend fun fetchDailyPlan(dayId: Int): DailyContent? {
        dailyPlanCache[dayId]?.let { return it }

        var planEntity = appDao.getPlanForDay(dayId)

        if (planEntity == null) {
            try {
                val remotePlan = firebaseDataSource.getDailyPlan(dayId)
                if (remotePlan != null) {
                    planEntity = remotePlan.toEntity(dayId)
                    appDao.insertPlan(planEntity)
                } else {
                    Log.w(TAG, "No remote plan found in Firebase for dayId=$dayId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch daily plan for dayId=$dayId", e)
            }
        }

        val domainPlan = planEntity?.toDomain()
        if (domainPlan != null) {
            dailyPlanCache[dayId] = domainPlan
        }
        return domainPlan
    }

    override suspend fun addExtraTask(title: String) {
        val randomId = Random.nextInt(1000, 99999)
        val todayDate = getTodayDate()
        val extraTask = ExtraTaskEntity(id = randomId, date = todayDate, title = title)
        appDao.insertExtraTask(extraTask)
    }

    override suspend fun completeDailyActivity(activity: Activity) {
        val todayDate = getTodayDate()
        val newStatus = !activity.isCompleted
        updateActivityStatus(todayDate, activity.id, newStatus)
    }

    override suspend fun updateActivityStatus(date: String, activityId: Int, isCompleted: Boolean) {
        appDao.updateOrInsertRecord(DailyRecordEntity(date, activityId, isCompleted))
        updateUserStats(isCompleted)
    }

    override suspend fun getSuggestedExtraTasks(): ExtraTasks {
        val todayId = getDayOfYear()

        val futurePlans = try {
            firebaseDataSource.getFuturePlans(todayId, 7)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch future plans starting from dayId=$todayId", e)
            emptyList()
        }

        val extraChallenges = mutableListOf<String>()
        val extraWerd = mutableListOf<String>()
        futurePlans.forEach { plan ->
            if (plan.quranDhuhr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranDhuhr}")
            if (plan.quranAsr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranAsr}")
            if (plan.quranMaghrib.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranMaghrib}")

            if (plan.challenge.isNotEmpty()) extraChallenges.add("تحدي إضافي: ${plan.challenge}")
        }

        return ExtraTasks(
            werd = extraWerd,
            challenges = extraChallenges
        )
    }

    private suspend fun updateUserStats(isCompleted: Boolean) {
        val currentStats = appDao.getUserStats().firstOrNull() ?: UserStatsEntity()
        val todayDate = getTodayDate()

        val starChange = if (isCompleted) 10 else -10
        val newTotalStars = (currentStats.totalStars + starChange).coerceAtLeast(0)
        val newTotalTasks =
            if (isCompleted) currentStats.tasksCompletedTotal + 1 else currentStats.tasksCompletedTotal

        val newTreeStage = when {
            newTotalStars < 50 -> 1
            newTotalStars < 200 -> 2
            else -> 3
        }

        var newStreak = currentStats.currentStreak
        var lastDate = currentStats.lastActiveDate

        if (isCompleted && currentStats.lastActiveDate != todayDate) {
            newStreak = if (isYesterday(currentStats.lastActiveDate)) newStreak + 1 else 1
            lastDate = todayDate
        }

        val updatedStats = currentStats.copy(
            totalStars = newTotalStars,
            tasksCompletedTotal = newTotalTasks,
            treeStage = newTreeStage,
            currentStreak = newStreak,
            lastActiveDate = lastDate
        )

        appDao.insertUserStats(updatedStats)

        try {
            if (updatedStats.userId.isNotEmpty()) {
                val userRankDto = com.menna.nabatan_7asena.data.remote.dto.UserRankDto(
                    userId = updatedStats.userId,
                    name = updatedStats.name,
                    gender = updatedStats.gender,
                    totalStars = updatedStats.totalStars
                )
                firebaseDataSource.uploadUserScore(userRankDto)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload user score", e)
        }
    }

    private fun buildQuranWerdTitle(): String {
        val bookmark = quranBookmarkStore.getBookmark() ?: return "إكمال من سورة الفاتحة"
        val surahName = QuranConstants.surahs.getOrNull(bookmark.surahId - 1)?.name
            ?: return "إكمال من عند آخر علامة حفظ"
        return "إكمال من سورة $surahName الآية ${bookmark.ayahNo}"
    }

    private fun getDayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    private fun getTodayDate(): String = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())

    private fun isYesterday(dateString: String): Boolean {
        if (dateString.isEmpty()) return false
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = sdf.format(calendar.time)
            dateString == yesterday
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val TAG = "DailyActivityRepo"
    }
}