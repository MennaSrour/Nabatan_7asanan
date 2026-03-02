package com.menna.nabata_7asena.data.repository

import com.menna.nabata_7asena.data.local.room.AppDao
import com.menna.nabata_7asena.data.local.room.DailyRecordEntity
import com.menna.nabata_7asena.data.local.room.ExtraTaskEntity
import com.menna.nabata_7asena.data.local.room.UserStatsEntity
import com.menna.nabata_7asena.data.mappers.toDomain
import com.menna.nabata_7asena.data.mappers.toEntity
import com.menna.nabata_7asena.data.remote.FirebaseDataSource
import com.menna.nabata_7asena.domain.GetPrayerTimesUseCase
import com.menna.nabata_7asena.domain.entity.Activity
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
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
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase
) : DailyActivityRepository {

    private val dailyPlanCache = mutableMapOf<Int, DailyContent?>()

    override fun getDailyActivities(
        date: String,
        city: String,
        country: String
    ): Flow<List<Activity>> {
        val recordsFlow = appDao.getRecordsForDate(date)
        val extraTasksFlow = appDao.getExtraTasksForDate(date)

        val dailyPlanFlow = flow {
            val dayId = getDayOfYear()

            val cachedPlan = dailyPlanCache[dayId]
            if (cachedPlan != null) {
                emit(cachedPlan)
                return@flow
            }

            var planEntity = appDao.getPlanForDay(dayId)

            if (planEntity == null) {
                try {
                    val remotePlan = firebaseDataSource.getDailyPlan(dayId)
                    if (remotePlan != null) {
                        planEntity = remotePlan.toEntity(dayId)
                        appDao.insertPlan(planEntity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val domainPlan: DailyContent? = planEntity?.toDomain()
            dailyPlanCache[dayId] = domainPlan
            emit(domainPlan)
        }

        return combine(recordsFlow, dailyPlanFlow, extraTasksFlow) { records, plan, extraTasks ->
            val statusMap = records.associate { it.itemId to it.isCompleted }
            val prayers = getPrayerTimesUseCase(city, country, date)
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


            if (plan != null) {
                activities.add(
                    Activity.Task(
                        200,
                        statusMap[200] ?: false,
                        plan.challengeTitle,
                        TaskCategory.CHALLENGE
                    )
                )

                val azkarList = listOf(
                    400 to "أذكار اليوم: سبحان الله 📿",
                    401 to "أذكار اليوم: الحمد لله 📿",
                    402 to "أذكار اليوم: سبحان الله وبحمده 📿",
                    403 to "أذكار اليوم: الله أكبر 📿",
                    404 to "أذكار اليوم: لا إله إلا الله 📿",
                    405 to "أذكار اليوم: لا حول ولا قوة إلا بالله 📿"
                )

                azkarList.forEach { (id, title) ->
                    activities.add(
                        Activity.Task(
                            id,
                            statusMap[id] ?: false,
                            title,
                            TaskCategory.AZKAR
                        )
                    )
                }

                val quran = plan.quranWerd
                activities.add(
                    Activity.Task(
                        301,
                        statusMap[301] ?: false,
                        "ورد الظهر: ${quran?.dhuhr ?: ""}",
                        TaskCategory.QURAN
                    )
                )
                activities.add(
                    Activity.Task(
                        302,
                        statusMap[302] ?: false,
                        "ورد العصر: ${quran?.asr ?: ""}",
                        TaskCategory.QURAN
                    )
                )
                activities.add(
                    Activity.Task(
                        303,
                        statusMap[303] ?: false,
                        "ورد المغرب: ${quran?.maghrib ?: ""}",
                        TaskCategory.QURAN
                    )
                )
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
        val planEntity = appDao.getPlanForDay(dayId)
        return planEntity?.toDomain()
    }

    override suspend fun getPlanForDay(dayId: Int): DailyContent? {
        var plan = appDao.getPlanForDay(dayId)

        if (plan == null) {
            try {
                val remotePlan = firebaseDataSource.getDailyPlan(dayId)
                if (remotePlan != null) {
                    plan = remotePlan.toEntity(dayId)
                    appDao.insertPlan(plan)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return plan?.toDomain()
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

        val futurePlans = firebaseDataSource.getFuturePlans(todayId, 7)

        val extraChallenges = mutableListOf<String>()
        val extraWerd = mutableListOf<String>()
        futurePlans.forEach { plan ->

            if (plan.quranDhuhr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranDhuhr}")
            if (plan.quranAsr.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranAsr}")
            if (plan.quranMaghrib.isNotEmpty()) extraWerd.add("تلاوة إضافية: ${plan.quranMaghrib}")


            if (plan.challenge.isNotEmpty()) extraChallenges.add("تحدي إضافي: ${plan.challenge}")
        }
        val suggestions = ExtraTasks(
            werd = extraWerd,
            challenges = extraChallenges
        )
        return suggestions
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
            if (isYesterday(currentStats.lastActiveDate)) {
                newStreak += 1
            } else if (currentStats.lastActiveDate.isEmpty()) {
                newStreak = 1
            } else {
                newStreak = 1
            }
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
                val userRankDto = com.menna.nabata_7asena.data.remote.dto.UserRankDto(
                    userId = updatedStats.userId,
                    name = updatedStats.name,
                    gender = updatedStats.gender,
                    totalStars = updatedStats.totalStars
                )


                firebaseDataSource.uploadUserScore(userRankDto)
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
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
}