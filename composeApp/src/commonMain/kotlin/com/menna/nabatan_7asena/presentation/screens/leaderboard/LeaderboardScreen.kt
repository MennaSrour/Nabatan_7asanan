package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.presentation.screens.friends.*
import kotlinx.coroutines.CancellationException
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.leaderboard.composables.LeaderboardContent

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = platformLeaderboardViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) { is LeaderboardEffect.ShowMessage -> snackbar.showSnackbar(effect.message) }
    }
    val friends = LocalFriendsViewModel.current
    if (friends == null) {
        LeaderboardContent(state, snackbar)
        return
    }
    var friendsOnly by rememberSaveable { mutableStateOf(false) }
    val displayed = if (friendsOnly) friendsLeaderboardState(friends, state) else state
    LeaderboardContent(displayed, snackbar) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(!friendsOnly, { friendsOnly = false }, { Text("الكل") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FriendsGold, selectedLabelColor = FriendsPurple))
            FilterChip(friendsOnly, { friendsOnly = true }, { Text("أصحابي") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FriendsGold, selectedLabelColor = FriendsPurple))
        }
    }
}

@Composable
private fun friendsLeaderboardState(model: FriendsViewModel, global: LeaderboardUiState): LeaderboardUiState {
    val friends by model.state.collectAsStateWithLifecycle()
    val user by model.user.collectAsStateWithLifecycle()
    val account = friends.account
    val localUser = user
    val accepted = friends.accepted
    var entries by remember(account, accepted, localUser) { mutableStateOf<List<LeaderboardEntry>?>(null) }
    var failed by remember(account, accepted, localUser) { mutableStateOf(false) }
    LaunchedEffect(account, accepted, localUser) {
        if (account == null || localUser == null || account.profile.profileId != localUser.id) return@LaunchedEffect
        try {
            entries = model.ranking(account, accepted, localUser)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            failed = true
        }
    }
    val loading = friends.loading || (account != null && !friends.failed && entries == null && !failed)
    return global.copy(
        list = entries.orEmpty(),
        isLoading = loading,
        currentUserRank = entries?.firstOrNull { it.userId == global.currentUserId }?.rank?.toLong(),
        isRankLoading = loading,
        isRankUnavailable = failed || friends.failed
    )
}
