package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.platform.platformQuranViewModel
import com.menna.nabatan_7asena.presentation.screens.quran.composables.*

private enum class JourneyDestination { KHATMAH, MEMORIZATION }

@Composable
fun QuranScreen(
    viewModel: QuranViewModel = platformQuranViewModel(),
    initialWirdPage: Int? = null,
    openKhatmah: Boolean = false
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val journey by viewModel.journey.collectAsStateWithLifecycle()
    val wirdPage by viewModel.requestedWirdPage.collectAsStateWithLifecycle()
    var destination by rememberSaveable {
        mutableStateOf(if (openKhatmah) JourneyDestination.KHATMAH else null)
    }
    val layout = rememberQuranLayoutState(state.savedPage)
    LaunchedEffect(viewModel, initialWirdPage) {
        initialWirdPage?.let(viewModel::openWirdPage)
    }
    QuranScreenEffects(state, layout, viewModel, wirdPage)
    QuranContent(
        state, viewModel, layout,
        onKhatmah = { destination = JourneyDestination.KHATMAH },
        onMemorization = { destination = JourneyDestination.MEMORIZATION }
    )

    val screen = destination
    val progress = journey
    if (screen != null) {
        QuranJourneyDialog(
            title = if (screen == JourneyDestination.KHATMAH) "ختمتي" else "رحلة الحفظ",
            onClose = { destination = null },
            snackbar = layout.snackbar
        ) {
            when {
                progress == null || (screen == JourneyDestination.MEMORIZATION && !state.isReaderReady) ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                screen == JourneyDestination.KHATMAH -> KhatmahPlanner(
                    progress,
                    onStart = viewModel::startKhatmah,
                    onRead = { page ->
                        destination = null
                        viewModel.openWirdPage(page)
                    },
                    onComplete = { viewModel.completeWird(progress.today, it) }
                )
                else -> {
                    val catalog = remember(state.mushafPages) { memorizationCatalog(state.mushafPages) }
                    MemorizationJourney(
                        state = progress,
                        catalog = catalog,
                        onReview = { surah ->
                            destination = null
                            viewModel.selectSurah(surah)
                        },
                        onPractice = { surah ->
                            destination = null
                            viewModel.selectSurah(surah)
                            viewModel.onOpenRepeatBottomSheet(surah)
                        },
                        onUpdate = viewModel::updateMemorization
                    )
                }
            }
        }
    }

    if (progress?.progress?.celebrationPending == true) {
        KhatmahCelebrationDialog(
            onDismiss = viewModel::dismissKhatmahCelebration,
            onStartNew = {
                destination = JourneyDestination.KHATMAH
                viewModel.dismissKhatmahCelebration()
            }
        )
    }
}
