package com.menna.nabata_7asena.presentation.screens.home

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.core.util.DateUtils
import com.menna.nabata_7asena.domain.LocationTracker
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.Activity
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.domain.entity.User
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val currentLocation: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _suggestedTasks = MutableStateFlow(ExtraTasks(emptyList(), emptyList()))
    val suggestedTasks = _suggestedTasks.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    private var mediaPlayer: MediaPlayer? = null
    private val playingTaskId = MutableStateFlow<Int?>(null)
    private var lastCompletedCount = -1
    private val todayDate = getTodayDate()
    override fun onCleared() {
        super.onCleared()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
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
        playingTaskId
    ) { domainUser, cityCountryPair, dailyContent, currentPlayingId ->
        val (city, country) = cityCountryPair
        val activitiesFlow = getDailyActivitiesUseCase(todayDate, city, country)

        Triple(domainUser, dailyContent, Pair(activitiesFlow, currentPlayingId))
    }
        .flatMapLatest { (user, dailyContent, flowPair) ->
            val (activitiesFlow, currentPlayingId) = flowPair
            activitiesFlow.map { domainActivities ->
                val currentPartToRead = user.quranPartsFinished + 1

                val uiTasks = domainActivities.map { activity ->
                    val uiModel = activity.toUiModel(currentPartToRead)
                    uiModel.copy(isPlaying = uiModel.id == currentPlayingId)
                }

                val currentCompletedCount = uiTasks.count { it.isCompleted }

                HomeUiState(
                    isLoading = false,
                    user = user.toUiUser(),
                    dailyWisdom = dailyContent?.wisdom ?: "نور اليوم: الصلاة نور والطاعة ضياء ✨",
                    tasks = uiTasks,
                    hijriDate = DateUtils.getCurrentHijriDate(offsetDays = -1),
                    showCelebration = false
                ) to currentCompletedCount
            }
        }
        .distinctUntilChanged { old, new -> old.first == new.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState() to -1)

    init {
        observeCombined()
    }

    private fun observeCombined() {
        viewModelScope.launch {
            combinedUiState.collect { (state, completedCount) ->
                val allTasksCompleted =
                    state.tasks.isNotEmpty() && state.tasks.all { it.isCompleted }

                val allPrayersReachableOrDone = state.tasks
                    .filter { it.emoji == "🕌" }
                    .all { prayer -> prayer.isCompleted || isPrayerTimeReached(prayer.subtitle) }

                val isReallyAllDone = allTasksCompleted && allPrayersReachableOrDone

                if (lastCompletedCount != -1 && completedCount > lastCompletedCount && isReallyAllDone) {
                    if (!_uiState.value.showCelebration) {
                        delay(500)
                        _uiState.update { it.copy(showCelebration = true) }
                    }
                }

                lastCompletedCount = completedCount
                _uiState.update {
                    it.copy(
                        isLoading = state.isLoading,
                        user = state.user,
                        dailyWisdom = state.dailyWisdom,
                        tasks = state.tasks,
                        hijriDate = state.hijriDate
                    )
                }
            }
        }
    }

    fun onTaskChecked(item: HomeUiState.UiTaskItem) {
        viewModelScope.launch {
            if (item.emoji == "🕌") {
                val prayerTime = item.subtitle
                if (!item.isCompleted && prayerTime != null && !isPrayerTimeReached(prayerTime)) {
                    _uiMessage.emit("لسه ميعاد الصلاة مجاش يا بطل! استعد واتوضأ 💧")
                    return@launch
                }
            }

            val newStatus = !item.isCompleted
            val res = toggleActivityStatusUseCase(todayDate, item.id, newStatus)

            if (res is Resource.Success) {
                if (item.emoji == "📖") {
                    if (newStatus) incrementQuranPartUseCase()
                    else decrementQuranPartUseCase()
                }
            }
        }
    }

    fun playTaskSound(context: Context, item: HomeUiState.UiTaskItem) {
        if (item.category == TaskCategory.EXTRA) return
        val soundRes = if (item.emoji == "🕌") {
            when (item.title) {
                "الفجر" -> R.raw.fajr
                "الظهر" -> R.raw.zuhr
                "العصر" -> R.raw.asr
                "المغرب" -> R.raw.sound_normal
                "العشاء" -> R.raw.ishaa
                else -> R.raw.sound_normal
            }
        } else {
            when (item.category) {
                TaskCategory.QURAN -> R.raw.werd

                TaskCategory.AZKAR -> when {
                    item.title.contains("سبحان الله وبحمده") -> R.raw.subhan_allah_wa_behamdeh
                    item.title.contains("لا حول ولا قوة إلا بالله") -> R.raw.la_hawla
                    item.title.contains("لا إله إلا الله") -> R.raw.la_ilah_ila_allah
                    item.title.contains("استغفر الله") -> R.raw.yala_zekr
                    item.title.contains("الله أكبر") -> R.raw.allahu_akbar
                    item.title.contains("الحمد لله") ||
                            item.title.contains("الحمدلله") -> R.raw.alhamdulillah

                    item.title.contains("سبحان الله") -> R.raw.subhan_allah
                    else -> R.raw.yala_zekr
                }

                TaskCategory.CHALLENGE -> R.raw.mohima

                else -> null
            }
        }

        if (soundRes == null) return

        try {
            mediaPlayer?.let { if (it.isPlaying) it.stop(); it.release() }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }

        try {
            mediaPlayer = MediaPlayer.create(context, soundRes)?.apply {
                setOnCompletionListener {
                    playingTaskId.value = null
                    try {
                        release()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mediaPlayer = null
                }
                playingTaskId.value = item.id
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            playingTaskId.value = null
            mediaPlayer = null
        }
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

    private fun Activity.toUiModel(currentPart: Int): HomeUiState.UiTaskItem {
        val isDone = isCompleted
        return when (this) {
            is Activity.Prayer -> HomeUiState.UiTaskItem(
                id = id,
                title = name,
                subtitle = time,
                isCompleted = isDone,
                backgroundColor = if (isDone) getDoneColor() else getPrayerColor(name),
                contentColor = if (isDone) Color.Gray.copy(alpha = 0.6f) else Color.White,
                emoji = "🕌",
                category = TaskCategory.PRAYER,
                isPlaying = false
            )

            is Activity.Task -> HomeUiState.UiTaskItem(
                id = id,
                title = title,
                subtitle = null,
                isCompleted = isDone,
                backgroundColor = if (isDone) getDoneColor() else getTaskColor(this.category),
                contentColor = if (isDone) Color.Gray.copy(alpha = 0.6f) else Color.White,
                emoji = getTaskEmoji(this.category),
                category = this.category,
                isPlaying = false
            )
        }
    }

    private fun getDoneColor() = Color(0xFFE8F5E9)
    private fun getPrayerColor(name: String) = when (name) {
        "الفجر" -> Color(0xFF0E6EBB)
        "الظهر" -> Color(0xFF9CBD77)
        "العصر" -> Color(0xFFC0A44F)
        "المغرب" -> Color(0xFF0B5C9B)
        "العشاء" -> Color(0xFF041056)
        else -> Color.Gray
    }

    private fun getTaskColor(category: TaskCategory) = when (category) {
        TaskCategory.QURAN -> Color(0xFF26A69A)
        TaskCategory.CHALLENGE -> Color(0xFF42A5F5)
        TaskCategory.AZKAR -> Color(0xFFFF7043)
        TaskCategory.EXTRA -> Color(0xFF7E57C2)
        TaskCategory.PRAYER -> Color(0xFF0E6EBB)
    }

    private fun getDoneGradient() =
        Brush.linearGradient(listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8)))

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

    private val _showChallengeDialog = MutableStateFlow(false)
    val showChallengeDialog = _showChallengeDialog.asStateFlow()

    private val _currentRiddle = MutableStateFlow<DailyContent.Riddle?>(null)
    val currentRiddle = _currentRiddle.asStateFlow()

    fun openDailyChallenge() {
        viewModelScope.launch {
            val todayRiddle = getDailyActivitiesUseCase.getTodayRiddle()
            if (todayRiddle != null && todayRiddle.riddle != null) {
                _currentRiddle.value = todayRiddle.riddle
                _showChallengeDialog.value = true
            }
        }
    }

    fun closeChallenge() {
        _showChallengeDialog.value = false
    }

    fun onCorrectAnswer() {
        viewModelScope.launch {
            delay(1500)
            closeChallenge()
        }
    }

    private fun getPrayerGradient(name: String) = when (name) {
        "الفجر" -> Brush.linearGradient(listOf(Color(0xFF0E6EBB), Color(0xFF156081)))
        "الظهر" -> Brush.linearGradient(listOf(Color(0xFF9CBD77), Color(0xFF559809)))
        "العصر" -> Brush.linearGradient(listOf(Color(0xFFC0A44F), Color(0xFFEA910E)))
        "المغرب" -> Brush.linearGradient(listOf(Color(0xFFEF5350), Color(0xFFC62828)))
        "العشاء" -> Brush.linearGradient(listOf(Color(0xFFAB47BC), Color(0xFF7B1FA2)))
        else -> Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
    }

    private fun getTaskEmoji(category: TaskCategory) = when (category) {
        TaskCategory.QURAN -> "📖"
        TaskCategory.CHALLENGE -> "💪"
        TaskCategory.AZKAR -> "📿"
        TaskCategory.EXTRA -> "✨"
        TaskCategory.PRAYER -> "🕌"
    }

    private fun getTaskGradient(category: TaskCategory) = when (category) {
        TaskCategory.QURAN -> Brush.linearGradient(listOf(Color(0xFF26A69A), Color(0xFF00897B)))
        TaskCategory.CHALLENGE -> Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)))
        TaskCategory.AZKAR -> Brush.linearGradient(listOf(Color(0xFFFF7043), Color(0xFFF4511E)))
        TaskCategory.EXTRA -> Brush.linearGradient(listOf(Color(0xFF7E57C2), Color(0xFF5E35B1)))
        TaskCategory.PRAYER -> getPrayerGradient("الفجر")
    }
}