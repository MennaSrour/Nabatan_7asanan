package com.menna.nabatan_7asena.platform
import com.menna.nabatan_7asena.presentation.screens.rewards.KidsRewardsViewModel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.menna.nabatan_7asena.presentation.MainViewModel
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaViewModel
import com.menna.nabatan_7asena.presentation.screens.update.UpdateViewModel
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingViewModel
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizViewModel
import com.menna.nabatan_7asena.presentation.screens.leaderboard.LeaderboardViewModel
import com.menna.nabatan_7asena.presentation.screens.quran.QuranViewModel
import com.menna.nabatan_7asena.presentation.screens.dua.DuaViewModel
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsViewModel
import com.menna.nabatan_7asena.presentation.screens.start.StartViewModel
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesViewModel
import com.menna.nabatan_7asena.presentation.screens.login.LoginViewModel
import com.menna.nabatan_7asena.presentation.screens.home.HomeViewModel

@HiltViewModel
class AndroidMainViewModel @Inject constructor(services: SharedServices) : MainViewModel(services.getCurrentUserUseCase, services.scheduling)

@Composable
actual fun platformMainViewModel(): MainViewModel = hiltViewModel<AndroidMainViewModel>()

@HiltViewModel
class AndroidSebhaViewModel @Inject constructor(services: SharedServices) : SebhaViewModel(services.newScreenAudio())

@Composable
actual fun platformSebhaViewModel(): SebhaViewModel = hiltViewModel<AndroidSebhaViewModel>()

@HiltViewModel
class AndroidUpdateViewModel @Inject constructor(services: SharedServices) : UpdateViewModel(services.checkForUpdateUseCase)

@Composable
actual fun platformUpdateViewModel(): UpdateViewModel = hiltViewModel<AndroidUpdateViewModel>()

@HiltViewModel
class AndroidOnboardingViewModel @Inject constructor(services: SharedServices) : OnboardingViewModel(services.permissions)

@Composable
actual fun platformOnboardingViewModel(): OnboardingViewModel = hiltViewModel<AndroidOnboardingViewModel>()

@HiltViewModel
class AndroidQuizViewModel @Inject constructor(services: SharedServices) : QuizViewModel(services.getWeeklyStudyQuestionsUseCase, services.getWeeklyCompetitionQuestionsUseCase, services.isCompetitionDayUseCase, services.officialQuizRepository, services.getRecentQuizWinnersUseCase, services.newScreenAudio())

@Composable
actual fun platformQuizViewModel(): QuizViewModel = hiltViewModel<AndroidQuizViewModel>()

@HiltViewModel
class AndroidLeaderboardViewModel @Inject constructor(services: SharedServices) : LeaderboardViewModel(services.getLeaderboardUseCase, services.getCurrentUserUseCase, services.getCurrentUserRankUseCase)

@Composable
actual fun platformLeaderboardViewModel(): LeaderboardViewModel = hiltViewModel<AndroidLeaderboardViewModel>()

@HiltViewModel
class AndroidQuranViewModel @Inject constructor(services: SharedServices) : QuranViewModel(services.saveQuranProgressUseCase, services.getQuranProgressUseCase, services.quranRepository, services.quranAudio, services.downloads, services.bookmarks, services.quranLibrary, services.quranJourney)

@Composable
actual fun platformQuranViewModel(): QuranViewModel = hiltViewModel<AndroidQuranViewModel>()

@HiltViewModel
class AndroidDuaViewModel @Inject constructor(services: SharedServices) : DuaViewModel(services.audio)

@Composable
actual fun platformDuaViewModel(): DuaViewModel = hiltViewModel<AndroidDuaViewModel>()

@HiltViewModel
class AndroidSettingsViewModel @Inject constructor(services: SharedServices) : SettingsViewModel(services.userRepository, services.resetAppUseCase, services.updateUserNameUseCase, services.updateProfileImageUseCase)

@Composable
actual fun platformSettingsViewModel(): SettingsViewModel = hiltViewModel<AndroidSettingsViewModel>()

@HiltViewModel
class AndroidStartViewModel @Inject constructor(services: SharedServices) : StartViewModel(services.dao, services.newScreenAudio())

@Composable
actual fun platformStartViewModel(): StartViewModel = hiltViewModel<AndroidStartViewModel>()

@HiltViewModel
class AndroidBadgesViewModel @Inject constructor(services: SharedServices) : BadgesViewModel(services.getCurrentUserUseCase, services.gardenRepository, services.achievementProgress)

@Composable
actual fun platformBadgesViewModel(): BadgesViewModel = hiltViewModel<AndroidBadgesViewModel>()

@HiltViewModel
class AndroidLoginViewModel @Inject constructor(services: SharedServices) : LoginViewModel(services.registerUserUseCase, services.newScreenAudio())

@Composable
actual fun platformLoginViewModel(): LoginViewModel = hiltViewModel<AndroidLoginViewModel>()

@HiltViewModel
class AndroidHomeViewModel @Inject constructor(services: SharedServices) : HomeViewModel(services.getDailyActivitiesUseCase, services.getCurrentUserUseCase, services.toggleActivityStatusUseCase, services.addExtraTaskUseCase, services.getSuggestedTasksUseCase, services.incrementQuranPartUseCase, services.decrementQuranPartUseCase, services.location, services.audio, services.quranRepository, services.getPrayerTimesUseCase, services.gardenRepository, services.quranJourney, services.dailyRepository)

@Composable
actual fun platformHomeViewModel(): HomeViewModel = hiltViewModel<AndroidHomeViewModel>()


@HiltViewModel
class AndroidKidsRewardsViewModel @Inject constructor(services: SharedServices) :
    KidsRewardsViewModel(services.userRepository, services.kidsRewardsRepository)

@Composable
actual fun platformKidsRewardsViewModel(): KidsRewardsViewModel = hiltViewModel<AndroidKidsRewardsViewModel>()
