package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.lifecycle.ViewModelStore
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.FirstLaunchRepository
import com.menna.nabatan_7asena.domain.usecase.ResetAppUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateProfileImageUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateUserNameUseCase
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.testsupport.FakeUserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeUserRepository()
    private val store = ViewModelStore()
    private lateinit var viewModel: SettingsViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        val firstLaunch = object : FirstLaunchRepository { override fun resetToFirstLaunch() = Unit }
        viewModel = SettingsViewModel(repository, ResetAppUseCase(repository, firstLaunch),
            UpdateUserNameUseCase(repository), UpdateProfileImageUseCase(repository))
        store.put("settings", viewModel)
    }

    @After fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun loadsSavedGenderAndAvatarAndOpensPicker() = runTest(dispatcher) {
        runCurrent()
        assertEquals(User.Gender.GIRL, viewModel.uiState.value.gender)
        assertEquals(AvatarCatalog.girlAvatars[1], viewModel.uiState.value.avatarResId)
        viewModel.onEditAvatarClicked()
        assertTrue(viewModel.uiState.value.showImagePickerDialog)
        viewModel.onImagePickerDismissed()
        assertFalse(viewModel.uiState.value.showImagePickerDialog)
    }

    @Test fun selectionPersistsAndIsRestoredByANewViewModel() = runTest(dispatcher) {
        runCurrent()
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(4)
        advanceUntilIdle()
        assertEquals(listOf(4), repository.avatarWrites)
        assertEquals(AvatarCatalog.girlAvatars[4], viewModel.uiState.value.avatarResId)
        assertFalse(viewModel.uiState.value.showImagePickerDialog)
        assertFalse(viewModel.uiState.value.isSavingAvatar)
        val recreated = SettingsViewModel(repository,
            ResetAppUseCase(repository, object : FirstLaunchRepository { override fun resetToFirstLaunch() = Unit }),
            UpdateUserNameUseCase(repository), UpdateProfileImageUseCase(repository))
        store.put("recreated", recreated)
        runCurrent()
        assertEquals(4, recreated.uiState.value.avatarIndex)
    }

    @Test fun failureKeepsSavedAvatarAndAllowsRetry() = runTest(dispatcher) {
        runCurrent()
        repository.saveAvatar = { throw IllegalStateException("Disk full") }
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(3)
        advanceUntilIdle()
        assertEquals(SettingsEffect.AvatarSaveFailed, viewModel.effects.first())
        assertEquals(1, viewModel.uiState.value.avatarIndex)
        assertTrue(viewModel.uiState.value.showImagePickerDialog)
        assertFalse(viewModel.uiState.value.isSavingAvatar)
        repository.saveAvatar = { repository.user.value = repository.user.value.copy(avatarIndex = it) }
        viewModel.onAvatarSelected(3)
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.avatarIndex)
    }

    @Test fun repeatedTapsDuringSaveProduceOneWriteAndPreserveOtherUpdates() = runTest(dispatcher) {
        runCurrent()
        val completion = CompletableDeferred<Unit>()
        repository.saveAvatar = { completion.await() }
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(2)
        viewModel.onAvatarSelected(3)
        runCurrent()
        assertEquals(listOf(2), repository.avatarWrites)
        repository.user.value = repository.user.value.copy(name = "Updated", prayerNotifications = false)
        runCurrent()
        completion.complete(Unit)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.avatarIndex)
        assertEquals("Updated", viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.prayerNotifications)
    }

    @Test fun closedPickerAndInvalidIndicesCannotWrite() = runTest(dispatcher) {
        runCurrent()
        viewModel.onAvatarSelected(2)
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(-1)
        viewModel.onAvatarSelected(AvatarCatalog.girlAvatars.size)
        advanceUntilIdle()
        assertTrue(repository.avatarWrites.isEmpty())
    }

    @Test fun choosingCurrentAvatarClosesPickerWithoutSaving() = runTest(dispatcher) {
        runCurrent()
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(1)
        advanceUntilIdle()
        assertTrue(repository.avatarWrites.isEmpty())
        assertFalse(viewModel.uiState.value.showImagePickerDialog)
    }

    @Test fun saveForOldProfileCannotReplaceNewProfilesAvatar() = runTest(dispatcher) {
        runCurrent()
        val completion = CompletableDeferred<Unit>()
        repository.saveAvatar = { completion.await() }
        viewModel.onEditAvatarClicked()
        viewModel.onAvatarSelected(4)
        runCurrent()
        repository.user.value = repository.user.value.copy(id = "other", gender = User.Gender.BOY, avatarIndex = 2)
        runCurrent()
        completion.complete(Unit)
        advanceUntilIdle()
        assertEquals(AvatarCatalog.boyAvatars[2], viewModel.uiState.value.avatarResId)
        assertFalse(viewModel.uiState.value.isSavingAvatar)
    }
}
