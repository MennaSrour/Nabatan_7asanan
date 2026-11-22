package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.lifecycle.ViewModelStore
import com.menna.nabatan_7asena.platform.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OnboardingPermissionsTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private class Permissions(override val supportsAndroidPermissions: Boolean = true) : PermissionPlatform {
        var granted = false
        override fun locationGranted() = granted
        override fun notificationsGranted() = granted
        override fun exactAlarmsGranted() = granted
        override fun batteryExempt() = granted
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun cleanup() { store.clear(); Dispatchers.resetMain() }
    private fun model(permissions: Permissions = Permissions(), persisted: () -> Unit = {}) =
        OnboardingViewModel(permissions, persisted).also { store.put("onboarding", it) }

    @Test fun batteryReturnWithoutExemptionAllowsContinueAndDoesNotLoop() = runTest(dispatcher) {
        var completions = 0
        val vm = model(persisted = { completions++ })
        vm.updatePageIndex(6)
        vm.onNextClicked()
        assertTrue(vm.uiState.value.showBatteryExplanation)
        vm.confirmBatteryExplanation()
        assertEquals(OnboardingEffect.RequestPermission(AppPermission.BATTERY), vm.effects.first())
        vm.onPermissionResult(AppPermission.BATTERY)
        vm.onNextClicked()
        assertEquals(OnboardingEffect.NavigateToLogin, vm.effects.first())
        vm.onNextClicked()
        assertEquals(1, completions)
        assertFalse(vm.uiState.value.showBatteryExplanation)
    }

    @Test fun grantedBatteryNeverOpensThePrompt() = runTest(dispatcher) {
        val permissions = Permissions().apply { granted = true }
        val vm = model(permissions)
        vm.updatePageIndex(6)
        vm.refreshPermissions()
        runCurrent()
        assertTrue(vm.uiState.value.permissions.battery)
        vm.onNextClicked()
        assertEquals(OnboardingEffect.NavigateToLogin, vm.effects.first())
    }

    @Test fun eachPermissionCanBeSkippedWithoutSkippingTheRemainingChoices() = runTest(dispatcher) {
        val vm = model()
        vm.onSkipClicked()
        assertEquals(3, vm.uiState.value.currentPage)
        for (index in 3..5) { vm.onSkipClicked(); assertEquals(index + 1, vm.uiState.value.currentPage) }
        vm.onSkipClicked()
        assertEquals(OnboardingEffect.NavigateToLogin, vm.effects.first())
    }

    @Test fun locationNotificationAndExactAlarmDenialsCanAdvanceOnContinue() = runTest(dispatcher) {
        val vm = model()
        listOf(AppPermission.LOCATION, AppPermission.NOTIFICATIONS, AppPermission.EXACT_ALARMS).forEachIndexed { index, permission ->
            vm.updatePageIndex(index + 3)
            vm.onNextClicked()
            assertEquals(OnboardingEffect.RequestPermission(permission), vm.effects.first())
            vm.onPermissionResult(permission)
            assertEquals(index + 3, vm.uiState.value.currentPage)
            vm.onNextClicked()
            assertEquals(index + 4, vm.uiState.value.currentPage)
        }
    }

    @Test fun iosOnboardingOffersLocationAndNotificationsOnly() = runTest(dispatcher) {
        val vm = model(Permissions(false))
        assertEquals(5, vm.uiState.value.pages.size)
        vm.updatePageIndex(4)
        vm.onSkipClicked()
        assertEquals(OnboardingEffect.NavigateToLogin, vm.effects.first())
    }
}
