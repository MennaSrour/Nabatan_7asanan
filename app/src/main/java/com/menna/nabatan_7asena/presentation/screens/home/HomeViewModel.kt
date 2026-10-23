package com.menna.nabatan_7asena.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import com.menna.nabatan_7asena.core.util.DateUtils
import com.menna.nabatan_7asena.core.util.PrayerSlot
import com.menna.nabatan_7asena.core.util.PrayerTimeline
import com.menna.nabatan_7asena.domain.LocationTracker
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.Activity
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.QuranRepository
import com.menna.nabatan_7asena.domain.usecase.AddExtraTaskUseCase
import com.menna.nabatan_7asena.domain.usecase.DecrementQuranPartUseCase
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabatan_7asena.domain.usecase.GetDailyActivitiesUseCase
import com.menna.nabatan_7asena.domain.usecase.GetSuggestedTasksUseCase
import com.menna.nabatan_7asena.domain.usecase.IncrementQuranPartUseCase
import com.menna.nabatan_7asena.domain.usecase.ToggleActivityStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyActivitiesUseCase: GetDailyActivitiesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleActivityStatusUseCase: ToggleActivityStatusUseCase,
    private val addExtraTaskUseCase: AddExtraTaskUseCase,
    private val getSuggestedTasksUseCase: GetSuggestedTasksUseCase,
    private val incrementQuranPartUseCase: IncrementQuranPartUseCase,
    private val decrementQuranPartUseCase: DecrementQuranPartUseCase,
    private val currentLocation: LocationTracker,
    private val audioPlayerManager: AudioPlayerManager,
    private val quranRepository: QuranRepository
) : ViewModel(), HomeActions {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiMessage = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _uiMessage.receiveAsFlow()

    private val playingTaskId = MutableStateFlow<Int?>(null)
    private val tempCompletedTaskIds = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    private var lastCompletedCount = -1

    private val retrySignal = MutableStateFlow(0)

    private val _hasLoadError = MutableStateFlow(false)

    override fun onTaskClicked(item: HomeUiState.UiTaskItem) {
        when (item.category) {
            TaskCategory.QURAN -> _uiMessage.trySend(HomeEffect.NavigateToQuran)
            TaskCategory.AZKAR -> _uiMessage.trySend(HomeEffect.NavigateToSebha)
            else -> onTaskChecked(item)
        }
    }

    override fun retryLoadingData() {
        retrySignal.update { it + 1 }
    }
    private fun currentDate() = getTodayDate()

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stopAnyPlayingSound()
    }

    private val minuteTicker = flow {
        while (true) {
            emit(Unit)
            delay(60_000L - System.currentTimeMillis() % 60_000L)
        }
    }
    private val dateFlow: Flow<String> = minuteTicker
        .map { currentDate() }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val combinedUiState = combine(dateFlow, retrySignal) { today, _ -> today }.flatMapLatest { today ->
        combine(
            getCurrentUserUseCase(),

            flow {
                emit(Pair("Cairo", "Egypt"))
                val location = kotlinx.coroutines.withTimeoutOrNull(3000) {
                    currentLocation.getCurrentLocation()
                }
                if (location != null) {
                    emit(Pair(location.city ?: "Cairo", location.country ?: "Egypt"))
                }
            },

            flow {
                emit(null as DailyContent?)
                val riddle = kotlinx.coroutines.withTimeoutOrNull(5000) {
                    getDailyActivitiesUseCase.getTodayRiddle()
                }
                if (riddle != null) {
                    emit(riddle)
                }
            },

            playingTaskId
        ) { domainUser, cityCountryPair, dailyContent, currentPlayingId ->
            val (city, country) = cityCountryPair
            val activitiesFlow = getDailyActivitiesUseCase(today, city, country)
                .onEach { _hasLoadError.value = false }
                .catch { _hasLoadError.value = true; emit(emptyList()) }
            Triple(domainUser, dailyContent, Pair(activitiesFlow, currentPlayingId))
        }
            .flatMapLatest { (user, dailyContent, flowPair) ->
            val (activitiesFlow, currentPlayingId) = flowPair

            combine(activitiesFlow, tempCompletedTaskIds, minuteTicker) { domainActivities, tempStatusMap, _ ->
                val currentPartToRead = user.quranPartsFinished + 1
                val nowMinutes = PrayerTimeline.nowMinutes()

                val baseTasks = domainActivities.map { activity ->
                    val uiModel = activity.toUiModel(currentPartToRead)
                    val tempStatus = tempStatusMap[uiModel.id]
                    val isDone = tempStatus ?: uiModel.isCompleted

                    uiModel.copy(
                        isCompleted = isDone,
                        isPlaying = uiModel.id == currentPlayingId
                    )
                }

                val prayerStatuses = PrayerTimeline.resolve(
                    prayers = baseTasks
                        .filter { it.category == TaskCategory.PRAYER }
                        .map { PrayerSlot(it.id, PrayerTimeline.parseMinutes(it.subtitle)) },
                    nowMinutes = nowMinutes
                )
                val uiTasks = baseTasks.map { task ->
                    val status = prayerStatuses[task.id] ?: return@map task
                    task.copy(prayerPhase = status.phase, isPrayerFocused = status.isFocused)
                }

                val currentCompletedCount = uiTasks.count { it.isCompleted }
                val hasPrayerLoadError = !_hasLoadError.value && uiTasks.none { it.category == TaskCategory.PRAYER }

                HomeUiState(
                    isLoading = false,
                    user = user.toUiUser(),
                    dailyWisdom = dailyContent?.wisdom ?: "نور اليوم: المؤمن للمؤمن كالبنيان يشد بعضه بعضاً",
                    tasks = uiTasks,
                    hijriDate = DateUtils.getCurrentHijriDate(offsetDays = 0),
                    isNightTheme = computeIsNightTheme(uiTasks, nowMinutes),
                    hasLoadError = _hasLoadError.value,
                    hasPrayerLoadError = hasPrayerLoadError
                ) to currentCompletedCount
            }
        }
    }
        .distinctUntilChanged { old, new -> old.first == new.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState() to -1)

    init {
        observeCombined()
        observeQuranBookmark()
    }

    private fun observeCombined() {
        viewModelScope.launch {
            combinedUiState.collect { (state, completedCount) ->
                val allTasksCompleted = state.tasks.isNotEmpty() && state.tasks.all { it.isCompleted }

                if (lastCompletedCount != -1 && completedCount > lastCompletedCount && allTasksCompleted) {
                    if (!_uiState.value.showCelebration) {
                        delay(500)
                        audioPlayerManager.playCelebrationSound()
                        _uiState.update { it.copy(showCelebration = true) }
                    }
                }

                lastCompletedCount = completedCount

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = state.isLoading,
                        user = state.user,
                        dailyWisdom = state.dailyWisdom,
                        tasks = state.tasks,
                        hijriDate = state.hijriDate,
                        isNightTheme = state.isNightTheme,
                        hasLoadError = state.hasLoadError,
                        hasPrayerLoadError = state.hasPrayerLoadError
                    )
                }
            }
        }
    }

    private fun observeQuranBookmark() {
        viewModelScope.launch {
            quranRepository.getBookmarkStatusFlow().collect { hasBookmark ->
                _uiState.update { it.copy(hasBookmark = hasBookmark) }
            }
        }
    }

    fun markTaskCompletedExternally(taskId: Int) {
        viewModelScope.launch {
            val alreadyDone = _uiState.value.tasks.firstOrNull { it.id == taskId }?.isCompleted == true
            if (alreadyDone) return@launch

            tempCompletedTaskIds.update { it + (taskId to true) }
            val res = toggleActivityStatusUseCase(currentDate(), taskId, true)
            if (res is Resource.Success) {
                val task = _uiState.value.tasks.firstOrNull { it.id == taskId }
                if (task?.category == TaskCategory.QURAN) {
                    incrementQuranPartUseCase()
                }
            }
            tempCompletedTaskIds.update { it - taskId }
        }
    }

    override fun onTaskChecked(item: HomeUiState.UiTaskItem) {
        viewModelScope.launch {
            if (item.category == TaskCategory.PRAYER) {
                val prayerTime = item.subtitle
                if (!item.isCompleted && prayerTime != null && !isPrayerTimeReached(prayerTime)) {
                    sendMessage("لا يجوز الصلاة قبل دخول موعدها")
                    return@launch
                }
            }

            val newStatus = !item.isCompleted
            tempCompletedTaskIds.update { it + (item.id to newStatus) }

            viewModelScope.launch {
                delay(300)
                val res = toggleActivityStatusUseCase(currentDate(), item.id, newStatus)

                if (res is Resource.Success) {
                    if (item.category == TaskCategory.QURAN) {
                        if (newStatus) incrementQuranPartUseCase()
                        else decrementQuranPartUseCase()
                    }
                }
                tempCompletedTaskIds.update { it - item.id }
            }
        }
    }

    override fun playTaskSound(item: HomeUiState.UiTaskItem) {
        audioPlayerManager.stopAnyPlayingSound()
        playingTaskId.value = item.id
        audioPlayerManager.playTaskSound(item) {
            playingTaskId.value = null
        }
    }

    override fun playWelcomeSound() {
        audioPlayerManager.playWelcomeSound()
    }

    override fun loadSuggestions() {
        viewModelScope.launch {
            when (val res = getSuggestedTasksUseCase()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(suggestions = res.data) }
                }
                is Resource.Error -> {
                    sendMessage(res.throwable?.message ?: "Failed to load suggestions")
                }
                else -> {}
            }
        }
    }

    override fun onAddExtraTask(title: String) {
        viewModelScope.launch {
            val res = addExtraTaskUseCase(title)
            if (res is Resource.Error) {
                sendMessage(res.throwable?.message ?: "Failed to add task")
            }
        }
    }

    override fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }

    override fun openDailyChallenge() {
        viewModelScope.launch {
            val todayRiddle = getDailyActivitiesUseCase.getTodayRiddle()
            if (todayRiddle != null && todayRiddle.riddle != null) {
                _uiState.update {
                    it.copy(currentRiddle = todayRiddle.riddle, showChallengeDialog = true)
                }
            }
        }
    }

    override fun closeChallenge() {
        _uiState.update { it.copy(showChallengeDialog = false) }
    }

    override fun onCorrectAnswer() {
        viewModelScope.launch {
            audioPlayerManager.playCorrectAnswerSound()
            delay(1500)
            closeChallenge()
        }
    }

    private fun Activity.toUiModel(currentPart: Int): HomeUiState.UiTaskItem {
        val isDone = isCompleted
        return when (this) {
            is Activity.Prayer -> HomeUiState.UiTaskItem(
                id = id,
                title = name,
                subtitle = time,
                isCompleted = isDone,
                emoji = "🕌",
                category = TaskCategory.PRAYER,
                isPlaying = false
            )
            is Activity.Task -> HomeUiState.UiTaskItem(
                id = id,
                title = title,
                subtitle = null,
                isCompleted = isDone,
                emoji = getTaskEmoji(this.category),
                category = this.category,
                isPlaying = false
            )
        }
    }

    private fun computeIsNightTheme(tasks: List<HomeUiState.UiTaskItem>, nowMinutes: Int): Boolean {
        val fajrMinutes = PrayerTimeline.parseMinutes(tasks.firstOrNull { it.title == "الفجر" }?.subtitle)
        ?: return false
        val maghribMinutes = PrayerTimeline.parseMinutes(tasks.firstOrNull { it.title == "المغرب" }?.subtitle)
        ?: return false

        return if (maghribMinutes > fajrMinutes) {
            nowMinutes >= maghribMinutes || nowMinutes < fajrMinutes
        } else {
            nowMinutes in fajrMinutes until maghribMinutes
        }
    }

    private fun isPrayerTimeReached(prayerTimeStr: String?): Boolean {
        val minutes = PrayerTimeline.parseMinutes(prayerTimeStr) ?: return true
        return PrayerTimeline.nowMinutes() >= minutes
    }

    private fun User.toUiUser() = HomeUiState.UiUser(
        name,
        if (gender == User.Gender.BOY) "👦" else "👧",
        "$totalStars ⭐",
        "$currentStreak أيام"
    )

    private fun getTodayDate() = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())

    private fun getTaskEmoji(category: TaskCategory) = when (category) {
        TaskCategory.QURAN -> "📖"
        TaskCategory.CHALLENGE -> "💪"
        TaskCategory.AZKAR -> "📿"
        TaskCategory.EXTRA -> "✨"
        TaskCategory.PRAYER -> "🕌"
    }
    private suspend fun sendMessage(message: String) {
        _uiMessage.send(HomeEffect.ShowMessage(message))
    }

}
