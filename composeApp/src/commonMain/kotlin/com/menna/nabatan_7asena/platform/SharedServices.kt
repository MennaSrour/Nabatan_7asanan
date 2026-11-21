package com.menna.nabatan_7asena.platform

import com.menna.nabatan_7asena.domain.*
import com.menna.nabatan_7asena.domain.repository.*
import com.menna.nabatan_7asena.domain.usecase.*
import com.menna.nabatan_7asena.data.*
import com.menna.nabatan_7asena.data.repository.*
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.data.local.*
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.update.*
import com.menna.nabatan_7asena.core.audio.*
import com.menna.nabatan_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import kotlinx.coroutines.flow.first

class SharedServices(
    val dao: AppDao,
    val location: LocationTracker,
    val audio: AudioPlayerManager,
    val quranAudio: QuranAudioPlayer,
    val downloads: QuranAudioDownloadManager,
    val alarms: GlobalAlarmManager,
    val permissions: PermissionPlatform,
    val scheduling: SchedulingPlatform,
    val newScreenAudio: () -> ScreenAudioPlayer
) {
    val firebase = FirebaseDataSource()
    val gardenRepository: GardenRepository = GardenRepositoryImpl(backup = firebase::backupGarden)
    val bookmarks = QuranBookmarkStore()
    val quranLibrary = QuranLibraryStore()
    val firstLaunch: FirstLaunchRepository = FirstLaunchManager()
    val prayerRepository: PrayerTimesRepository = PrayerTimesRepositoryImpl(PrayerTimesApi())
    val getPrayerTimesUseCase = GetPrayerTimesUseCase(prayerRepository)
    val userRepository: UserRepository = UserRepositoryImpl(dao, firebase, alarms, scheduling)
    val getCurrentUserUseCase = GetCurrentUserUseCase(userRepository)
    val dailyRepository: DailyActivityRepository = DailyActivityRepositoryImpl(dao, firebase, getPrayerTimesUseCase, bookmarks)
    val quranRepository: QuranRepository = QuranRepositoryImpl(dao, bookmarks)
    private val officialAttemptStore = OfficialAttemptStore()
    val achievementProgress = LocalAchievementProgress(dao, officialAttemptStore)
    val officialQuizRepository: OfficialQuizRepository = OfficialQuizRepositoryImpl(firebase, { getCurrentUserUseCase().first() }, officialAttemptStore)
    val quizRepository: QuizRepository = QuizRepositoryImpl(firebase, officialQuizRepository)
    val updateRepository: UpdateRepository = UpdateRepositoryImpl(UpdateRemoteDataSource())
    val addExtraTaskUseCase = AddExtraTaskUseCase(dailyRepository)
    val checkForUpdateUseCase = CheckForUpdateUseCase(updateRepository)
    val decrementQuranPartUseCase = DecrementQuranPartUseCase(userRepository)
    val getDailyActivitiesUseCase = GetDailyActivitiesUseCase(dailyRepository)
    val getLeaderboardUseCase = GetLeaderboardUseCase(userRepository)
    val getCurrentUserRankUseCase = GetCurrentUserRankUseCase(userRepository)
    val getQuranProgressUseCase = GetQuranProgressUseCase(quranRepository)
    val getRecentQuizWinnersUseCase = GetRecentQuizWinnersUseCase(quizRepository)
    val getSuggestedTasksUseCase = GetSuggestedTasksUseCase(dailyRepository)
    val getWeeklyCompetitionQuestionsUseCase = GetWeeklyCompetitionQuestionsUseCase(quizRepository)
    val getWeeklyStudyQuestionsUseCase = GetWeeklyStudyQuestionsUseCase(quizRepository)
    val incrementQuranPartUseCase = IncrementQuranPartUseCase(userRepository)
    val isCompetitionDayUseCase = IsCompetitionDayUseCase(quizRepository)
    val registerUserUseCase = RegisterUserUseCase(userRepository)
    val resetAppUseCase = ResetAppUseCase(userRepository, firstLaunch)
    val saveQuranProgressUseCase = SaveQuranProgressUseCase(quranRepository)
    val submitCompetitionScoreUseCase = SubmitCompetitionScoreUseCase(quizRepository)
    val toggleActivityStatusUseCase = ToggleActivityStatusUseCase(dailyRepository)
    val toggleNotificationsUseCase = ToggleNotificationsUseCase(userRepository)
    val updateProfileImageUseCase = UpdateProfileImageUseCase(userRepository)
    val updateUserNameUseCase = UpdateUserNameUseCase(userRepository)
}
