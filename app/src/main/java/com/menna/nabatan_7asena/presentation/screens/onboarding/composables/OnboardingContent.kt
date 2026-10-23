package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingActions
import com.menna.nabatan_7asena.presentation.screens.onboarding.OnboardingUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingContent(
    state: OnboardingUiState,
    actions: OnboardingActions
) {
    val currentPage = state.currentPage
    val pages = state.pages
    val pagerState = rememberPagerState(initialPage = currentPage, pageCount = { pages.size })
    val isDark = isSystemInDarkTheme()
    LaunchedEffect(pagerState.settledPage) { actions.updatePageIndex(pagerState.settledPage) }
    LaunchedEffect(currentPage) { pagerState.animateScrollToPage(currentPage) }
    if (state.showBatteryExplanation) {
        BatteryExplanationDialog(actions)
    }

    Scaffold(containerColor = SummerTheme.colors.dayBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OnboardingBackground(isDark)

            Column(modifier = Modifier.fillMaxSize()) {
                OnboardingPageIndicators(pages.size, currentPage)

                Spacer(Modifier.height(20.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { index ->
                    OnboardingPageContent(page = pages[index], pageIndex = index)
                }

                BottomSection(
                    currentPage = currentPage,
                    totalPages = pages.size,
                    onNextClicked = actions::onNextClicked,
                    onSkipClicked = actions::onSkipClicked
                )
            }
        }
    }
}
