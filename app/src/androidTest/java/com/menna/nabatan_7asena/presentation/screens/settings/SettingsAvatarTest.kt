package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.screens.settings.composables.SettingsImagePickerDialog
import com.menna.nabatan_7asena.presentation.screens.settings.composables.SettingsProfileHeader
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsAvatarTest {
    @get:Rule val compose = createComposeRule()

    @Test fun editIconOpensPickerAndSelectionDispatchesItsIndex() {
        val state = mutableStateOf(SettingsUiState(userId = "saved", gender = User.Gender.GIRL))
        var selectedIndex: Int? = null
        val actions = object : SettingsActions {
            override fun onEditAvatarClicked() { state.value = state.value.copy(showImagePickerDialog = true) }
            override fun onImagePickerDismissed() { state.value = state.value.copy(showImagePickerDialog = false) }
            override fun onAvatarSelected(imageIdentifier: Int) { selectedIndex = imageIdentifier }
            override fun toggleNotification(category: String, isEnabled: Boolean) = Unit
            override fun updateUserName(newName: String) = Unit
            override fun resetProgress() = Unit
        }
        compose.setContent {
            NabataAppTheme(isNight = false) {
                SettingsProfileHeader(state.value, actions)
                SettingsImagePickerDialog(state.value, actions)
            }
        }
        compose.onNodeWithContentDescription("تغيير الشخصية").performClick()
        compose.onNodeWithText("اختاري شخصيتك يا بطلة").assertIsDisplayed()
        compose.onNodeWithContentDescription("اختاري الصورة 2").performClick()
        compose.runOnIdle { assertEquals(1, selectedIndex) }
    }
}
