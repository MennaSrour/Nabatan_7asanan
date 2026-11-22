package com.menna.nabatan_7asena.presentation.screens.home

import com.menna.nabatan_7asena.data.repository.QuranJourneyRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import com.menna.nabatan_7asena.core.util.DateUtils
import com.menna.nabatan_7asena.core.util.getCurrentHijriDate
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
import com.menna.nabatan_7asena.platform.AppClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.LocalDate

open class HomeViewModel constructor(
    private val getDailyActivitiesUseCase: GetDailyActivitiesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleActivityStatusUseCase: ToggleActivityStatusUseCase,
    private val addExtraTaskUseCase: AddExtraTaskUseCase,
    private val getSuggestedTasksUseCase: GetSuggestedTasksUseCase,
    private val incrementQuranPartUseCase: IncrementQuranPartUseCase,
    private val decrementQuranPartUseCase: DecrementQuranPartUseCase,
    private val currentLocation: LocationTracker,
    private val audioPlayerManager: AudioPlayerManager,
    private val quranRepository: QuranRepository,
    private val getPrayerTimesUseCase: com.menna.nabatan_7asena.domain.GetPrayerTimesUseCase,
    private val gardenRepository: com.menna.nabatan_7asena.domain.repository.GardenRepository,
    private val quranJourney: QuranJourneyRepository,
    private val dailyActivityRepository: com.menna.nabatan_7asena.domain.repository.DailyActivityRepository,
    internal val ramadanCalendar: com.menna.nabatan_7asena.data.repository.RamadanCalendarRepository
) : ViewModel(), HomeActions {

    suspend fun awardPrayerGuideBonus(): Boolean {
        val user = getCurrentUserUseCase().first()
        check(user.id.isNotBlank() && user.id != "local")
        return dailyActivityRepository.awardPrayerGuideBonus(user.id)
    }

    internal val ramadanProfileIds = getCurrentUserUseCase().map { it.id }.distinctUntilChanged()

    private fun syncRamadanCalendar() = viewModelScope.launch {
        ramadanProfileIds.collectLatest { profileId ->
            if (profileId.isBlank() || profileId == "local") return@collectLatest
            val synced = mutableMapOf<Int, Set<Int>>()
            ramadanCalendar.observeBackups(profileId).distinctUntilChanged().collectLatest { years ->
                for ((year, completed) in years) {
                    val pending = completed - synced[year].orEmpty()
                    if (pending.isEmpty()) continue
                    while (true) {
                        try {
                            if (withTimeoutOrNull(10_000) {
                                    ramadanCalendar.backup(profileId, year, pending)
                                    true
                                } == true
                            ) {
                                synced[year] = completed
                                break
                            }
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            // Room retains the completion for a later retry.
                        }
                        delay(30_000)
                    }
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiMessage = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _uiMessage.receiveAsFlow()

    private val playingTaskId = MutableStateFlow<Int?>(null)
    private val tempCompletedTaskIds = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    private var lastCompletedCount = -1

    private var gardenSyncJob: Job? = null
    private var gardenSyncProfile: String? = null

    private val retrySignal = MutableStateFlow(0)

    private val _hasLoadError = MutableStateFlow(false)

    override fun onTaskClicked(item: HomeUiState.UiTaskItem) {
        when (item.category) {
            TaskCategory.QURAN -> _uiMessage.trySend(HomeEffect.NavigateToQuran(item.wird?.firstPage))
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
            delay(60_000L - AppClock.nowMillis() % 60_000L)
        }
    }
    private val dateFlow: Flow<LocalDate> = minuteTicker
        .map { AppClock.today() }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val combinedUiState = combine(dateFlow, retrySignal) { today, _ -> today }.flatMapLatest { date ->
        val today = AppClock.dateString(date)
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
            val activities = getDailyActivitiesUseCase(today, city, country)
                .onEach { _hasLoadError.value = false }
                .catch { _hasLoadError.value = true; emit(emptyList()) }
            val activitiesFlow = combine(activities, prayerBoundaries(date, city, country)) { tasks, bounds -> tasks to bounds }
            Triple(domainUser, dailyContent, Pair(activitiesFlow, currentPlayingId))
        }
            .flatMapLatest { (user, dailyContent, flowPair) ->
            val (activitiesFlow, currentPlayingId) = flowPair

            combine(activitiesFlow, tempCompletedTaskIds, minuteTicker, gardenRepository.observe(user.id),
                quranJourney.observe(user.id)) { activitiesAndBounds, tempStatusMap, _, observedGarden, journey ->
                val (domainActivities, bounds) = activitiesAndBounds
                val gardenResult = creditGarden(user.id, date, domainActivities, observedGarden)
                val garden = (gardenResult as? Resource.Success)?.data
                val currentPartToRead = user.quranPartsFinished + 1
                val nowMinutes = PrayerTimeline.nowMinutes()

                val baseTasks = domainActivities.map { activity ->
                    val uiModel = activity.toUiModel(currentPartToRead)
                    val tempStatus = tempStatusMap[uiModel.id]
                    val isDone = tempStatus ?: uiModel.isCompleted

                    val wird = if (uiModel.category == TaskCategory.QURAN) journey.wirdFor(date) else null
                    if (wird != null) {
                        uiModel.copy(
                            title = "ورد الختمة اليوم",
                            subtitle = "صفحة ${wird.firstPage} – ${wird.lastPage}",
                            isCompleted = journey.lastWirdDate == date,
                            wird = wird
                        )
                    } else uiModel.copy(
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
                    showKhatmahCelebration = journey.celebrationPending,
                    gardenProfileId = user.id,
                    gardenEarnedStars = user.totalStars,
                    garden = garden,
                    isGardenUnavailable = gardenResult is Resource.Error,
                    gardenNeedsWater = garden?.lastWateredDate?.let { it < date.toString() } ?: true,
                    prayerDate = date,
                    previousIshaMinutes = bounds.first,
                    tomorrowFajrMinutes = bounds.second,
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
        syncRamadanCalendar()
    }

    private fun prayerBoundaries(date: LocalDate, city: String, country: String) = flow {
        emit(Pair<Int?, Int?>(null, null))
        val bounds = coroutineScope {
            val previous = async {
                withTimeoutOrNull(5000) { getPrayerTimesUseCase.exact(city, country, AppClock.dateString(date.minus(1, DateTimeUnit.DAY))) }
            }
            val next = async {
                withTimeoutOrNull(5000) { getPrayerTimesUseCase.exact(city, country, AppClock.dateString(date.plus(1, DateTimeUnit.DAY))) }
            }
            PrayerTimeline.parseMinutes(previous.await()?.isha) to PrayerTimeline.parseMinutes(next.await()?.fajr)
        }
        emit(bounds)
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

                if (gardenSyncProfile != state.gardenProfileId) { gardenSyncJob?.cancel(); gardenSyncProfile = null }
                if (state.garden?.syncPending == true && !state.isGardenUnavailable) requestGardenSync(state.gardenProfileId)
                lastCompletedCount = completedCount

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = state.isLoading,
                        user = state.user,
                        dailyWisdom = state.dailyWisdom,
                        tasks = state.tasks,
                        showKhatmahCelebration = state.showKhatmahCelebration,
                        gardenProfileId = state.gardenProfileId,
                        gardenEarnedStars = state.gardenEarnedStars,
                        isGardenShopBusy = currentState.gardenProfileId == state.gardenProfileId && currentState.isGardenShopBusy,
                        gardenShopError = if (currentState.gardenProfileId == state.gardenProfileId) currentState.gardenShopError else null,
                        gardenPurchaseSequence = if (currentState.gardenProfileId == state.gardenProfileId) currentState.gardenPurchaseSequence else 0,
                        garden = state.garden,
                        isGardenUnavailable = state.isGardenUnavailable,
                        gardenNeedsWater = state.gardenNeedsWater,
                        isWateringGarden = currentState.gardenProfileId == state.gardenProfileId && currentState.isWateringGarden,
                        gardenWateredTreeId = if (currentState.gardenProfileId == state.gardenProfileId) currentState.gardenWateredTreeId else null,
                        gardenWateringSequence = if (currentState.gardenProfileId == state.gardenProfileId) currentState.gardenWateringSequence else 0,
                        gardenSaveFailed = currentState.gardenProfileId == state.gardenProfileId && currentState.gardenSaveFailed,
                        prayerDate = state.prayerDate,
                        previousIshaMinutes = state.previousIshaMinutes,
                        tomorrowFajrMinutes = state.tomorrowFajrMinutes,
                        hijriDate = state.hijriDate,
                        isNightTheme = state.isNightTheme,
                        hasLoadError = state.hasLoadError,
                        hasPrayerLoadError = state.hasPrayerLoadError
                    )
                }
            }
        }
    }

    private suspend fun creditGarden(
        profileId: String, date: LocalDate, activities: List<Activity>,
        observed: Resource<com.menna.nabatan_7asena.domain.entity.GardenProgress>
    ): Resource<com.menna.nabatan_7asena.domain.entity.GardenProgress> {
        if (observed is Resource.Error) return observed
        return try {
            Resource.Success(gardenRepository.creditTasks(profileId, date, activities.filter { it.isCompleted }.map { it.id }.toSet()))
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { Resource.Error(e) }
    }

    override fun buyGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration) =
        buyGardenProduct(com.menna.nabatan_7asena.domain.entity.GardenProduct.entries.first { it.id == item.id })
    override fun equipGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, equipped: Boolean) =
        updateGardenWorld { owner -> gardenRepository.equip(owner, item, equipped); false }
    override fun buyGardenProduct(product: com.menna.nabatan_7asena.domain.entity.GardenProduct) =
        updateGardenWorld { owner -> gardenRepository.buyProduct(owner, product).purchased }
    override fun plantGardenTree(treeId: String, plot: Int?) =
        updateGardenWorld { owner -> gardenRepository.plantTree(owner, treeId, plot); false }
    override fun showGardenTree(treeId: String, visible: Boolean) =
        updateGardenWorld { owner -> gardenRepository.showTree(owner, treeId, visible); false }
    override fun placeGardenDecoration(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, plot: Int) =
        updateGardenWorld { owner -> gardenRepository.placeDecoration(owner, item, plot); false }
    override fun manageGardenPlacement(id: String, plot: Int?, visible: Boolean?, remove: Boolean) =
        updateGardenWorld { owner -> gardenRepository.managePlacement(owner, id, plot, visible, remove); false }

    private fun updateGardenWorld(operation: suspend (String) -> Boolean) {
        val owner = _uiState.value.gardenProfileId
        if (owner.isBlank() || _uiState.value.isGardenShopBusy || _uiState.value.isGardenUnavailable) return
        _uiState.update { it.copy(isGardenShopBusy = true, gardenShopError = null) }
        viewModelScope.launch {
            try {
                val bought = operation(owner)
                _uiState.update { if (it.gardenProfileId != owner) it else it.copy(
                    gardenPurchaseSequence = it.gardenPurchaseSequence + if (bought) 1 else 0) }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                val error = when (e) {
                    is com.menna.nabatan_7asena.domain.entity.InsufficientGardenStars -> com.menna.nabatan_7asena.presentation.common.GenderedStrings.shopInsufficient
                    is com.menna.nabatan_7asena.domain.entity.GardenLandLimit -> com.menna.nabatan_7asena.presentation.common.GenderedStrings.gardenLandLimit
                    is com.menna.nabatan_7asena.domain.entity.GardenPlacementUnavailable -> com.menna.nabatan_7asena.presentation.common.GenderedStrings.gardenPlacementUnavailable
                    else -> com.menna.nabatan_7asena.presentation.common.GenderedStrings.shopSaveFailed
                }
                _uiState.update { if (it.gardenProfileId != owner) it else it.copy(gardenShopError = error) }
            } finally { _uiState.update { if (it.gardenProfileId == owner) it.copy(isGardenShopBusy = false) else it } }
        }
    }

    override fun openGarden() { _uiMessage.trySend(HomeEffect.NavigateToGarden) }

    override fun waterGarden(treeId: String?) {
        val current = _uiState.value
        if (current.isWateringGarden || current.gardenProfileId.isBlank() || (current.garden?.waterDrops ?: 0) <= 0) return
        _uiState.update { it.copy(isWateringGarden = true, gardenSaveFailed = false) }
        viewModelScope.launch {
            try {
                val next = gardenRepository.water(current.gardenProfileId, AppClock.today(), treeId)
                _uiState.update {
                    if (it.gardenProfileId != current.gardenProfileId) it
                    else it.copy(isWateringGarden = false, gardenWateredTreeId = if (next.applied) treeId ?: next.progress.trees.firstOrNull { tree -> tree.plotIndex != null }?.id else it.gardenWateredTreeId, gardenWateringSequence = it.gardenWateringSequence + if (next.applied) 1 else 0)
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                _uiState.update { if (it.gardenProfileId == current.gardenProfileId) it.copy(isWateringGarden = false, gardenSaveFailed = true) else it }
            }
        }
    }

    private fun requestGardenSync(profileId: String) {
        if (profileId.isBlank() || profileId == "local" || gardenSyncProfile == profileId && gardenSyncJob?.isActive == true) return
        gardenSyncJob?.cancel()
        gardenSyncProfile = profileId
        gardenSyncJob = viewModelScope.launch {
            while (isActive) {
                if (withTimeoutOrNull(7000) { gardenRepository.sync(profileId) } == true) break
                delay(60_000)
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
            val task = _uiState.value.tasks.firstOrNull { it.id == taskId }
            if (task?.isCompleted == true) return@launch
            if (task?.wird != null) {
                completeKhatmahTask(task)
                return@launch
            }

            tempCompletedTaskIds.update { it + (taskId to true) }
            val res = toggleActivityStatusUseCase(currentDate(), taskId, true)
            if (res is Resource.Success) {
                if (task?.category == TaskCategory.QURAN) {
                    incrementQuranPartUseCase()
                }
            }
            tempCompletedTaskIds.update { it - taskId }
        }
    }

    override fun onTaskChecked(item: HomeUiState.UiTaskItem) {
        if (item.wird != null) {
            if (!item.isCompleted) completeKhatmahTask(item)
            return
        }
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

    private fun completeKhatmahTask(item: HomeUiState.UiTaskItem) {
        val wird = item.wird ?: return
        val date = _uiState.value.prayerDate ?: return
        val id = _uiState.value.gardenProfileId
        viewModelScope.launch {
            try {
                quranJourney.complete(id, date, wird)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                sendMessage("تعذّر حفظ ورد اليوم. حاوِل مرة أخرى.")
            }
        }
    }

    fun dismissKhatmahCelebration(startNew: Boolean) {
        val id = _uiState.value.gardenProfileId
        viewModelScope.launch {
            try {
                quranJourney.dismissCelebration(id)
                if (startNew) _uiMessage.send(HomeEffect.NavigateToKhatmah)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                sendMessage("تعذّر حفظ التغيير. حاوِل مرة أخرى.")
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

    private var addingTask = false

    override fun onAddExtraTask(title: String) {
        if (addingTask) return
        addingTask = true
        viewModelScope.launch {
            try {
                val res = addExtraTaskUseCase(title, _uiState.value.tasks.map { it.title })
                if (res is Resource.Error) sendMessage(com.menna.nabatan_7asena.resources.getResourceString(
                    if (res.throwable is com.menna.nabatan_7asena.domain.util.DuplicateDailyTaskException)
                        com.menna.nabatan_7asena.resources.AppResources.string.task_duplicate
                    else com.menna.nabatan_7asena.resources.AppResources.string.task_add_failed))
            } finally { addingTask = false }
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
        audioPlayerManager.playCorrectAnswerSound()
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

    private fun getTodayDate() = AppClock.dateString()

    private fun getTaskEmoji(category: TaskCategory) = when (category) {
        TaskCategory.QURAN -> "📖"
        TaskCategory.AZKAR -> "📿"
        TaskCategory.EXTRA -> "✨"
        TaskCategory.PRAYER -> "🕌"
    }
    private suspend fun sendMessage(message: String) {
        _uiMessage.send(HomeEffect.ShowMessage(message))
    }

}
