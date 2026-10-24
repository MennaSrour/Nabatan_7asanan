package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.components.avatar.AvatarPreview
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.login.LoginActions
import com.menna.nabatan_7asena.presentation.screens.login.LoginUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoxScope.LoginForm(state: LoginUiState, actions: LoginActions, currentAvatarRes: Int, onEditAvatar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LoginWelcome()

        AvatarPreview(
            avatarRes = currentAvatarRes,
            onEditClick = onEditAvatar
        )

        Text(
            genderedStringResource(GenderedStrings.loginChooseAvatarHint),
            fontSize = 13.sp,
            color = SummerTheme.colors.wisdomAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onEditAvatar)
        )

        GenderToggleRow(
            selectedGender = state.selectedGender,
            onGenderSelected = { actions.onGenderSelected(it) }
        )

        LoginNameCard(state, actions)

        StartJourneyButton(
            isLoading = state.isLoading,
            onClick = { actions.onStartClicked() }
        )
    }
}
