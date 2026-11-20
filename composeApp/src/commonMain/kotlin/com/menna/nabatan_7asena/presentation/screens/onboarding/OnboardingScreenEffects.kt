package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.compose.runtime.Composable

@Composable
expect fun OnboardingScreenEffects(viewModel: OnboardingViewModel, onNavigateToLogin: () -> Unit)
