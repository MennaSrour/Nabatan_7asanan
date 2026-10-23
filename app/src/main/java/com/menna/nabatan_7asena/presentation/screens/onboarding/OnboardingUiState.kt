package com.menna.nabatan_7asena.presentation.screens.onboarding

import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings

data class OnboardingPage(
    val title: com.menna.nabatan_7asena.presentation.common.GenderedString,
    val description: com.menna.nabatan_7asena.presentation.common.GenderedString,
    val imageRes: Int
)

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = onboardingPages,
    val showBatteryExplanation: Boolean = false
)

internal val onboardingPages = listOf(
    OnboardingPage(
        title = GenderedStrings.onboardingWelcomeTitle,
        description = GenderedStrings.onboardingWelcomeDescription,
        imageRes = R.drawable.onboarding_home
    ),
    OnboardingPage(
        title = GenderedStrings.onboardingPrayerTitle,
        description = GenderedStrings.onboardingPrayerDescription,
        imageRes = R.drawable.onboarding_leaderboard
    ),
    OnboardingPage(
        title = GenderedStrings.onboardingAzkarTitle,
        description = GenderedStrings.onboardingAzkarDescription,
        imageRes = R.drawable.onboarding_sebha
    ),
    OnboardingPage(
        title = GenderedStrings.onboardingLocationTitle,
        description = GenderedStrings.onboardingLocationDescription,
        imageRes = R.drawable.onboarding_badges
    ),
    OnboardingPage(
        title = GenderedStrings.onboardingNotificationsTitle,
        description = GenderedStrings.onboardingNotificationsDescription,
        imageRes = R.drawable.onboarding_badges
    ),
    OnboardingPage(
        title = GenderedStrings.onboardingBackgroundTitle,
        description = GenderedStrings.onboardingBackgroundDescription,
        imageRes = R.drawable.onboarding_badges
    )
)
