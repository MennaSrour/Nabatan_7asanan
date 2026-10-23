package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.leaderboard.LeaderboardUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun LeaderboardContent(state: LeaderboardUiState, snackbarHostState: SnackbarHostState) {
    Box(modifier = Modifier.fillMaxSize()) {
        LeaderboardBackground()

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LeaderboardHeader()

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SummerTheme.colors.goldWarm)
                    }
                } else if (state.list.isEmpty()) {
                    LeaderboardEmptyState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            val topThree = state.list.take(3)
                            if (topThree.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                PodiumView(topThree, state.currentUserId)
                                Spacer(Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(SummerTheme.colors.textBrown.copy(0.25f))
                                    )
                                    Text(
                                        genderedStringResource(GenderedStrings.leaderboardGrowing),
                                        fontSize = 12.sp,
                                        color = SummerTheme.colors.textBrown,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(SummerTheme.colors.textBrown.copy(0.25f))
                                    )
                                }
                            }
                        }

                        val restOfList = state.list.drop(3)
                        itemsIndexed(restOfList) { index, user ->
                            LeaderboardItem(
                                user = user,
                                rank = index + 4,
                                isCurrentUser = user.userId == state.currentUserId
                            )
                        }
                    }
                }
            }
        }
    }
}
