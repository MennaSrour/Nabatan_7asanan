package com.menna.nabata_7asena.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.core.audio.AudioPlayerManager
import com.menna.nabata_7asena.core.util.DateUtils
import com.menna.nabata_7asena.domain.LocationTracker
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.Activity
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.domain.repository.QuranRepository
import com.menna.nabata_7asena.domain.usecase.AddExtraTaskUseCase
import com.menna.nabata_7asena.domain.usecase.DecrementQuranPartUseCase
import com.menna.nabata_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabata_7asena.domain.usecase.GetDailyActivitiesUseCase
import com.menna.nabata_7asena.domain.usecase.GetSuggestedTasksUseCase
import com.menna.nabata_7asena.domain.usecase.IncrementQuranPartUseCase
import com.menna.nabata_7asena.domain.usecase.ToggleActivityStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _suggestedTasks = MutableStateFlow(ExtraTasks(emptyList(), emptyList()))
    val suggestedTasks = _suggestedTasks.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    private val playingTaskId = MutableStateFlow<Int?>(null)
    private val tempCompletedTaskIds = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    private var lastCompletedCount = -1
    private val todayDate = getTodayDate()

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stopAnyPlayingSound()
    }

    private val minuteTicker = flow {
        while (true) {
            emit(Unit)
            delay(60_000)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val combinedUiState = combine(
        getCurrentUserUseCase(),
        flow {
            val location = currentLocation.getCurrentLocation()
            emit(Pair(location?.city ?: "Cairo", location?.country ?: "Egypt"))
        },
        flow { emit(getDailyActivitiesUseCase.getTodayRiddle()) },
        playingTaskId,
        minuteTicker
    ) { domainUser, cityCountryPair, dailyContent, currentPlayingId, _ ->
        val (city, country) = cityCountryPair
        val activitiesFlow = getDailyActivitiesUseCase(todayDate, city, country)

        Triple(domainUser, dailyContent, Pair(activitiesFlow, currentPlayingId))
    }
        .flatMapLatest { (user, dailyContent, flowPair) ->
            val (activitiesFlow, currentPlayingId) = flowPair
            combine(activitiesFlow, tempCompletedTaskIds) { domainActivities, tempStatusMap ->
                val currentPartToRead = user.quranPartsFinished + 1

                val uiTasks = domainActivities.map { activity ->
                    val uiModel = activity.toUiModel(currentPartToRead)
                    val tempStatus = tempStatusMap[uiModel.id]
                    val isDone = tempStatus ?: uiModel.isCompleted

                    uiModel.copy(
                        isCompleted = isDone,
                        isPlaying = uiModel.id == currentPlayingId
                    )
                }

                val currentCompletedCount = uiTasks.count { it.isCompleted }

                HomeUiState(
                    isLoading = false,
                    user = user.toUiUser(),
                    dailyWisdom = dailyContent?.wisdom ?: "نور اليوم: الصلاة نور والطاعة ضياء ✨",
                    tasks = uiTasks,
                    hijriDate = DateUtils.getCurrentHijriDate(offsetDays = 0),
                    isNightTheme = computeIsNightTheme(uiTasks)
                ) to currentCompletedCount
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

                val allPrayersReachableOrDone = state.tasks
                    .filter { it.category == TaskCategory.PRAYER }
                    .all { prayer -> prayer.isCompleted || isPrayerTimeReached(prayer.subtitle) }

                val isReallyAllDone = allTasksCompleted && allPrayersReachableOrDone

                if (lastCompletedCount != -1 && completedCount > lastCompletedCount && isReallyAllDone) {
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
                        isNightTheme = state.isNightTheme
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
            val res = toggleActivityStatusUseCase(todayDate, taskId, true)
            if (res is Resource.Success) {
                val task = _uiState.value.tasks.firstOrNull { it.id == taskId }
                if (task?.category == TaskCategory.QURAN) {
                    incrementQuranPartUseCase()
                }
            }
            tempCompletedTaskIds.update { it - taskId }
        }
    }

    fun onTaskChecked(item: HomeUiState.UiTaskItem) {
        viewModelScope.launch {
            if (item.category == TaskCategory.PRAYER) {
                val prayerTime = item.subtitle
                if (!item.isCompleted && prayerTime != null && !isPrayerTimeReached(prayerTime)) {
                    _uiMessage.emit("لسه ميعاد الصلاة مجاش يا بطل! استعد واتوضأ 💧")
                    return@launch
                }
            }

            val newStatus = !item.isCompleted
            tempCompletedTaskIds.update { it + (item.id to newStatus) }

            viewModelScope.launch {
                delay(300)
                val res = toggleActivityStatusUseCase(todayDate, item.id, newStatus)

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

    fun playTaskSound(item: HomeUiState.UiTaskItem) {
        audioPlayerManager.stopAnyPlayingSound()
        playingTaskId.value = item.id
        audioPlayerManager.playTaskSound(item) {
            playingTaskId.value = null
        }
    }

    fun playWelcomeSound() {
        audioPlayerManager.playWelcomeSound()
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            when (val res = getSuggestedTasksUseCase()) {
                is Resource.Success -> {
                    _suggestedTasks.value = res.data
                }
                is Resource.Error -> {
                    _uiMessage.emit(res.throwable?.message ?: "Failed to load suggestions")
                }
                else -> {}
            }
        }
    }

    fun onAddExtraTask(title: String) {
        viewModelScope.launch {
            val res = addExtraTaskUseCase(title)
            if (res is Resource.Error) {
                _uiMessage.emit(res.throwable?.message ?: "Failed to add task")
            }
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }

    fun openDailyChallenge() {
        viewModelScope.launch {
            val todayRiddle = getDailyActivitiesUseCase.getTodayRiddle()
            if (todayRiddle != null && todayRiddle.riddle != null) {
                _uiState.update {
                    it.copy(currentRiddle = todayRiddle.riddle, showChallengeDialog = true)
                }
            }
        }
    }

    fun closeChallenge() {
        _uiState.update { it.copy(showChallengeDialog = false) }
    }

    fun onCorrectAnswer() {
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

    private fun parsePrayerTime(prayerTimeStr: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat("h:mm a", Locale.US)
            val date = sdf.parse(prayerTimeStr.trim().uppercase()) ?: return null
            Calendar.getInstance().apply { time = date }
        } catch (e: Exception) {
            try {
                val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
                val date = sdf24.parse(prayerTimeStr.trim()) ?: return null
                Calendar.getInstance().apply { time = date }
            } catch (e2: Exception) {
                null
            }
        }
    }

    private fun computeIsNightTheme(tasks: List<HomeUiState.UiTaskItem>): Boolean {
        val fajrTime = tasks.firstOrNull { it.title == "الفجر" }?.subtitle ?: return false
        val maghribTime = tasks.firstOrNull { it.title == "المغرب" }?.subtitle ?: return false

        val fajrCal = parsePrayerTime(fajrTime) ?: return false
        val maghribCal = parsePrayerTime(maghribTime) ?: return false
        val now = Calendar.getInstance()

        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val fajrMinutes = fajrCal.get(Calendar.HOUR_OF_DAY) * 60 + fajrCal.get(Calendar.MINUTE)
        val maghribMinutes = maghribCal.get(Calendar.HOUR_OF_DAY) * 60 + maghribCal.get(Calendar.MINUTE)

        return if (maghribMinutes > fajrMinutes) {
            nowMinutes >= maghribMinutes || nowMinutes < fajrMinutes
        } else {
            nowMinutes in fajrMinutes until maghribMinutes
        }
    }

    private fun isPrayerTimeReached(prayerTimeStr: String?): Boolean {
        if (prayerTimeStr.isNullOrEmpty()) return true

        val prayerCal = parsePrayerTime(prayerTimeStr) ?: return true
        val now = Calendar.getInstance()

        val prayerHour = prayerCal.get(Calendar.HOUR_OF_DAY)
        val prayerMinute = prayerCal.get(Calendar.MINUTE)
        val nowHour = now.get(Calendar.HOUR_OF_DAY)
        val nowMinute = now.get(Calendar.MINUTE)

        if (nowHour > prayerHour) return true
        if (nowHour == prayerHour && nowMinute >= prayerMinute) return true

        return false
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
}