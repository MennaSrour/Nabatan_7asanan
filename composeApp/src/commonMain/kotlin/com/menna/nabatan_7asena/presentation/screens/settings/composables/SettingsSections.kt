package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.friends.LocalFriendsViewModel
import com.menna.nabatan_7asena.presentation.screens.friends.rememberFriendCodeCopier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSections(state: SettingsUiState, actions: SettingsActions, padding: androidx.compose.foundation.layout.PaddingValues, onEditName: () -> Unit, onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsProfileHeader(state, actions)
        SummerSectionTitle(genderedStringResource(GenderedStrings.settingsAccount))
        SummerSettingsItem(
            icon = Icons.Rounded.Edit,
            title = genderedStringResource(GenderedStrings.settingsEditName),
            subtitle = genderedStringResource(GenderedStrings.settingsChangeName),
            iconColor = SummerTheme.Colors.PrimaryGold,
            onClick = onEditName
        )

        if (state.userId != null) SettingsFriendCodeItem()

        Spacer(modifier = Modifier.height(24.dp))

        PrayerSettingsSection(state, actions)
        PermissionsSettingsSection(state, actions)
        SummerSectionTitle(genderedStringResource(GenderedStrings.settingsNotifications))

        NotificationSetting(GenderedStrings.settingsPrayerTimes, GenderedStrings.settingsPrayerHint, SummerTheme.colors.wisdomAccent, state.prayerNotifications) { actions.toggleNotification("prayer", it) }

        NotificationSetting(GenderedStrings.settingsAzkar, GenderedStrings.settingsAzkarHint, SummerTheme.colors.goldDeep, state.azkarNotifications) { actions.toggleNotification("azkar", it) }

        NotificationSetting(GenderedStrings.settingsQuran, GenderedStrings.settingsQuranHint, SummerTheme.Colors.TaskCompletedGreen, state.quranNotifications) { actions.toggleNotification("quran", it) }

        Spacer(modifier = Modifier.height(10.dp))

        SummerSectionTitle(genderedStringResource(GenderedStrings.settingsControls))
        SummerSettingsItem(
            icon = Icons.Rounded.DeleteForever,
            title = genderedStringResource(GenderedStrings.settingsNewJourney),
            subtitle = genderedStringResource(GenderedStrings.settingsResetHint),
            iconColor = SummerTheme.colors.errorRed,
            onClick = onReset
        )
    }
}


@Composable
private fun SettingsFriendCodeItem() {
    val model = LocalFriendsViewModel.current ?: return
    val state by model.state.collectAsStateWithLifecycle()
    val copyCode = rememberFriendCodeCopier()
    val code = state.account?.profile?.friendCode
    var copied by remember(code) { mutableStateOf(false) }
    val status = when {
        copied -> "تم نسخ الكود"
        code != null -> "اضغط لنسخ الكود"
        state.loading -> "جارٍ تجهيز الكود"
        state.failed -> "اضغط لإعادة المحاولة"
        else -> "الكود غير متاح الآن"
    }
    SummerSettingsItem(
        icon = Icons.Rounded.ContentCopy,
        title = "كود الصداقة",
        subtitle = if (code == null) status else "\u2066$code\u2069\n$status",
        iconColor = SummerTheme.colors.wisdomAccent,
        onClick = {
            if (code != null) {
                copyCode(code)
                copied = true
            } else if (state.failed) model.retry()
        }
    )
}
