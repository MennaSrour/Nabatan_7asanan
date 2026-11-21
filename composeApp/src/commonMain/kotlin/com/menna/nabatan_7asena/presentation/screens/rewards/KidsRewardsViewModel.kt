package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.KidsRewardsRepository
import com.menna.nabatan_7asena.domain.repository.UserRepository
import com.menna.nabatan_7asena.platform.AppClock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal data class RewardAccount(val profileId: String, val day: LocalDate)

internal sealed interface SpinState {
    data object Idle : SpinState
    data object Saving : SpinState
    data class Won(val prize: WheelPrize) : SpinState
    data object Failed : SpinState
}

internal data class GamePayout(
    val requested: Int,
    val score: Int?,
    val credited: Int? = null,
    val failed: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
open class KidsRewardsViewModel(
    users: UserRepository,
    private val rewards: KidsRewardsRepository
) : ViewModel() {
    private val day = flow {
        while (true) {
            emit(AppClock.today())
            delay(1_000)
        }
    }.distinctUntilChanged()

    internal val account = combine(users.getCurrentUser().map { it.id.ifBlank { "local" } }.distinctUntilChanged(), day, ::RewardAccount)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    internal val status = account.transformLatest<RewardAccount?, DailyRewardStatus?> { current ->
        emit(null)
        if (current != null) emitAll(rewards.observe(current.profileId, current.day))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _spin = MutableStateFlow<SpinState>(SpinState.Idle)
    internal val spin = _spin.asStateFlow()
    private val _payout = MutableStateFlow<GamePayout?>(null)
    internal val payout = _payout.asStateFlow()

    init {
        viewModelScope.launch {
            account.filterNotNull().collectLatest { current ->
                while (true) {
                    sync(current)
                    delay(30_000)
                }
            }
        }
    }

    internal fun spinWheel() {
        val current = account.value ?: return
        if (status.value?.prize != null || _spin.value == SpinState.Saving || _spin.value is SpinState.Won) return
        _spin.value = SpinState.Saving
        viewModelScope.launch {
            try {
                val prize = rewards.spin(current.profileId, current.day)
                _spin.value = if (prize == null) SpinState.Idle else SpinState.Won(prize)
                sync(current)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _spin.value = SpinState.Failed
            }
        }
    }

    internal fun dismissSpin() {
        if (_spin.value != SpinState.Saving) _spin.value = SpinState.Idle
    }

    internal fun resetGame() {
        if (_payout.value?.let { it.credited == null && !it.failed } != true) _payout.value = null
    }

    internal fun finishGame(current: RewardAccount, game: KidsGame, stars: Int, score: Int? = null) {
        val previous = _payout.value
        if (previous != null && !previous.failed) return
        val result = GamePayout(stars, score)
        _payout.value = result
        viewModelScope.launch {
            try {
                val amount = rewards.rewardGame(current.profileId, current.day, game, stars)
                _payout.value = result.copy(credited = amount)
                sync(current)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _payout.value = result.copy(failed = true)
            }
        }
    }

    private suspend fun sync(current: RewardAccount) {
        try {
            rewards.sync(current.profileId, current.day)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Pending rewards remain in Room for the next retry.
        }
    }

    internal suspend fun verses() = rewards.verses()
    internal suspend fun questions() = rewards.questions()
}
