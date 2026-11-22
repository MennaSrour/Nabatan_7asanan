package com.menna.nabatan_7asena.platform

import androidx.compose.runtime.Composable
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
expect fun platformMainViewModel(): MainViewModel
@Composable
expect fun platformSebhaViewModel(): SebhaViewModel
@Composable
expect fun platformUpdateViewModel(): UpdateViewModel
@Composable
expect fun platformOnboardingViewModel(): OnboardingViewModel
@Composable
expect fun platformQuizViewModel(): QuizViewModel
@Composable
expect fun platformLeaderboardViewModel(): LeaderboardViewModel
@Composable
expect fun platformQuranViewModel(): QuranViewModel
@Composable
expect fun platformDuaViewModel(): DuaViewModel
@Composable
expect fun platformSettingsViewModel(): SettingsViewModel
@Composable
expect fun platformStartViewModel(): StartViewModel
@Composable
expect fun platformBadgesViewModel(): BadgesViewModel
@Composable
expect fun platformLoginViewModel(): LoginViewModel
@Composable
expect fun platformHomeViewModel(): HomeViewModel

@Composable
internal expect fun platformFriendsViewModel(): com.menna.nabatan_7asena.presentation.screens.friends.FriendsViewModel

@androidx.compose.runtime.Composable
expect fun platformKidsRewardsViewModel(): com.menna.nabatan_7asena.presentation.screens.rewards.KidsRewardsViewModel
