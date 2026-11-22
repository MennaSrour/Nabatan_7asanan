package com.menna.nabatan_7asena.platform
import com.menna.nabatan_7asena.presentation.screens.rewards.KidsRewardsViewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
actual fun platformMainViewModel(): MainViewModel = viewModel { MainViewModel(iosServices.getCurrentUserUseCase, iosServices.scheduling) }

@Composable
actual fun platformSebhaViewModel(): SebhaViewModel = viewModel { SebhaViewModel(iosServices.newScreenAudio()) }

@Composable
actual fun platformUpdateViewModel(): UpdateViewModel = viewModel { UpdateViewModel(iosServices.checkForUpdateUseCase) }

@Composable
actual fun platformOnboardingViewModel(): OnboardingViewModel = viewModel { OnboardingViewModel(iosServices.permissions) }

@Composable
actual fun platformQuizViewModel(): QuizViewModel = viewModel { QuizViewModel(iosServices.getWeeklyStudyQuestionsUseCase, iosServices.getWeeklyCompetitionQuestionsUseCase, iosServices.isCompetitionDayUseCase, iosServices.officialQuizRepository, iosServices.getRecentQuizWinnersUseCase, iosServices.newScreenAudio()) }

@Composable
actual fun platformLeaderboardViewModel(): LeaderboardViewModel = viewModel { LeaderboardViewModel(iosServices.getLeaderboardUseCase, iosServices.getCurrentUserUseCase, iosServices.getCurrentUserRankUseCase) }

@Composable
actual fun platformQuranViewModel(): QuranViewModel = viewModel { QuranViewModel(iosServices.saveQuranProgressUseCase, iosServices.getQuranProgressUseCase, iosServices.quranRepository, iosServices.quranAudio, iosServices.downloads, iosServices.bookmarks, iosServices.quranLibrary, iosServices.quranJourney) }

@Composable
actual fun platformDuaViewModel(): DuaViewModel = viewModel { DuaViewModel(iosServices.audio) }

@Composable
actual fun platformSettingsViewModel(): SettingsViewModel = viewModel { SettingsViewModel(iosServices.userRepository, iosServices.resetAppUseCase, iosServices.updateUserNameUseCase, iosServices.updateProfileImageUseCase) }

@Composable
actual fun platformStartViewModel(): StartViewModel = viewModel { StartViewModel(iosServices.dao, iosServices.newScreenAudio()) }

@Composable
actual fun platformBadgesViewModel(): BadgesViewModel = viewModel { BadgesViewModel(iosServices.getCurrentUserUseCase, iosServices.gardenRepository, iosServices.achievementProgress) }

@Composable
actual fun platformLoginViewModel(): LoginViewModel = viewModel { LoginViewModel(iosServices.registerUserUseCase, iosServices.newScreenAudio()) }

@Composable
actual fun platformHomeViewModel(): HomeViewModel = viewModel { HomeViewModel(iosServices.getDailyActivitiesUseCase, iosServices.getCurrentUserUseCase, iosServices.toggleActivityStatusUseCase, iosServices.addExtraTaskUseCase, iosServices.getSuggestedTasksUseCase, iosServices.incrementQuranPartUseCase, iosServices.decrementQuranPartUseCase, iosServices.location, iosServices.audio, iosServices.quranRepository, iosServices.getPrayerTimesUseCase, iosServices.gardenRepository, iosServices.quranJourney, iosServices.dailyRepository, iosServices.ramadanCalendarRepository) }


@Composable
internal actual fun platformFriendsViewModel(): com.menna.nabatan_7asena.presentation.screens.friends.FriendsViewModel =
    viewModel { com.menna.nabatan_7asena.presentation.screens.friends.FriendsViewModel(iosServices) }

@Composable
actual fun platformKidsRewardsViewModel(): KidsRewardsViewModel =
    viewModel { KidsRewardsViewModel(iosServices.userRepository, iosServices.kidsRewardsRepository) }
