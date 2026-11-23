package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.platform.Log
import com.menna.nabatan_7asena.data.local.QuranBookmarkStore
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.ExtraTaskEntity
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random
import com.menna.nabatan_7asena.platform.AppClock
import com.menna.nabatan_7asena.domain.entity.DAILY_QURAN_TASK_ID
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class DailyActivityRepositoryImpl constructor(
    private val appDao: AppDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val quranBookmarkStore: QuranBookmarkStore
) : DailyActivityRepository {

    private val dailyPlanCache = mutableMapOf<Int, DailyContent>()

    override fun getDailyActivities(date: String, city: String, country: String): Flow<List<Activity>> =
        getDailyActivities(date, com.menna.nabatan_7asena.data.local.PrayerSettingsStore.state.value.request)

    override fun getDailyActivities(date: String, request: com.menna.nabatan_7asena.domain.entity.PrayerRequest?): Flow<List<Activity>> {
        val recordsFlow = appDao.getRecordsForDate(date)
        val extraTasksFlow = appDao.getExtraTasksForDate(date)

        return combine(recordsFlow, extraTasksFlow) { records, extraTasks ->
            val statusMap = records.associate { it.itemId to it.isCompleted }
            val prayers = try {
                getPrayerTimesUseCase.at(request, date)
            } catch (e: kotlinx.coroutines.CancellationException) { throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch prayer times for date=$date", e)
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
                    id = DAILY_QURAN_TASK_ID,
                    isCompleted = statusMap[DAILY_QURAN_TASK_ID] ?: false,
                    title = buildQuranWerdTitle(),
                    category = TaskCategory.QURAN
                )
            )

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

    private val addTaskMutex = Mutex()

    override suspend fun addExtraTask(title: String) = addTaskMutex.withLock {
        val todayDate = getTodayDate()
        val existing = appDao.getExtraTasksForDate(todayDate).firstOrNull().orEmpty()
        com.menna.nabatan_7asena.domain.util.requireNewDailyTask(title, existing.map { it.title })
        var randomId: Int
        do { randomId = Random.nextInt(1000, 99999) } while (existing.any { it.id == randomId })
        appDao.insertExtraTask(ExtraTaskEntity(id = randomId, date = todayDate, title = title.trim()))
    }

    override suspend fun completeDailyActivity(activity: Activity) {
        val todayDate = getTodayDate()
        val newStatus = !activity.isCompleted
        updateActivityStatus(todayDate, activity.id, newStatus)
    }

    override suspend fun updateActivityStatus(date: String, activityId: Int, isCompleted: Boolean) {
        val today = AppClock.today()
        appDao.saveActivity(date, activityId, isCompleted, today, today.minus(1, DateTimeUnit.DAY))
    }

    override suspend fun claimDailyCompletionReward(
        profileId: String,
        date: String,
        completedTaskIds: Set<Int>
    ): Boolean = appDao.claimDailyCompletionReward(profileId, date, completedTaskIds)

    override suspend fun getSuggestedExtraTasks(): ExtraTasks {
        val todayId = getDayOfYear()

        val futurePlans = try {
            firebaseDataSource.getFuturePlans(todayId, 7)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch future plans starting from dayId=$todayId", e)
            emptyList()
        }

        val extraWerd = mutableListOf<String>()
        futurePlans.forEach { plan ->
            if (plan.quranDhuhr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranDhuhr}")
            if (plan.quranAsr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranAsr}")
            if (plan.quranMaghrib.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranMaghrib}")

        }

        return ExtraTasks(
            werd = extraWerd,
            challenges = emptyList()
        )
    }

    private fun buildQuranWerdTitle(): String {
        val bookmark = quranBookmarkStore.getBookmark() ?: return "إكمال من سورة الفاتحة"
        val surahName = QuranConstants.surahs.getOrNull(bookmark.surahId - 1)?.name
            ?: return "إكمال من عند آخر علامة حفظ"
        return "إكمال من سورة $surahName الآية ${bookmark.ayahNo}"
    }

    private fun getDayOfYear(): Int = AppClock.dayOfYear()
    private fun getTodayDate(): String = AppClock.dateString()

    override suspend fun awardPrayerGuideBonus(userId: String) = appDao.awardPrayerGuideBonus(getTodayDate(), userId)

    companion object {
        private const val TAG = "DailyActivityRepo"
    }
}
