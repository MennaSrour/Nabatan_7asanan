package com.menna.nabatan_7asena.presentation.screens.friends

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.data.repository.*
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.PresetMessage
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.platform.Log
import com.menna.nabatan_7asena.platform.SharedServices
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal val LocalFriendsViewModel = staticCompositionLocalOf<FriendsViewModel?> { null }

internal data class FriendsState(
    val account: FriendsAccount? = null,
    val relationships: List<Friendship> = emptyList(),
    val loading: Boolean = true,
    val failed: Boolean = false
) {
    val incoming get() = relationships.filter { it.status == "pending" && it.recipientId == account?.uid }
    val outgoing get() = relationships.filter { it.status == "pending" && it.senderId == account?.uid }
    val accepted get() = relationships.filter { it.status == "accepted" }
}

@OptIn(ExperimentalCoroutinesApi::class)
open class FriendsViewModel internal constructor(services: SharedServices) : ViewModel() {
    private val repository = services.friendsRepository
    private val retryCount = MutableStateFlow(0)
    internal val user = services.getCurrentUserUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val identity = user.filterNotNull().distinctUntilChangedBy { Triple(it.id, it.gender, it.avatarIndex) }

    internal val state: StateFlow<FriendsState> = combine(identity, retryCount) { current, _ -> current }
        .transformLatest { current ->
            emit(FriendsState(loading = current.id != "local"))
            if (current.id == "local") return@transformLatest
            var account: FriendsAccount? = null
            try {
                val signedIn = repository.account(current)
                account = signedIn
                emit(FriendsState(signedIn))
                kotlinx.coroutines.coroutineScope {
                    launch {
                        repository.gifts(signedIn.uid)
                            .onEach { ids -> services.gardenRepository.creditFriendGifts(current.id, ids) }
                            .retryWhen { error, _ ->
                                Log.w("Friends", "Retrying Garden gift listener: ${error::class.simpleName}")
                                delay(10_000)
                                true
                            }.collect()
                    }
                    repository.relationships(signedIn.uid).collect { emit(FriendsState(signedIn, it, loading = false)) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Friends", "Unable to load friendships", e)
                emit(FriendsState(account, loading = false, failed = true))
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FriendsState())

    internal fun retry() = retryCount.update { it + 1 }
    internal suspend fun request(code: String) = repository.request(requireNotNull(state.value.account), code)
    internal suspend fun respond(relationship: Friendship, accept: Boolean) = repository.respond(relationship, accept)
    internal fun messages(relationship: Friendship) = repository.messagesFor(relationship.id)
    internal suspend fun peer(relationship: Friendship, account: FriendsAccount) = repository.peerProfile(relationship.peer(account.uid))
    internal suspend fun send(relationship: Friendship, preset: PresetMessage) =
        repository.send(requireNotNull(state.value.account), relationship, preset)
    internal suspend fun ranking(account: FriendsAccount, relationships: List<Friendship>, user: User): List<LeaderboardEntry> =
        repository.friendsRanking(account, relationships, user)
}
