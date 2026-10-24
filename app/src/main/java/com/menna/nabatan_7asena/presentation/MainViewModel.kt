package com.menna.nabatan_7asena.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.menna.nabatan_7asena.core.worker.DailyManagerWorker
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    getCurrentUser: GetCurrentUserUseCase
) : ViewModel() {

    val uiState = getCurrentUser()
        .map { MainUiState(gender = it.gender) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())


    
    fun enableDailyAutoScheduling() {
        val workManager = WorkManager.getInstance(context)

        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        
        val periodicRequest = PeriodicWorkRequestBuilder<DailyManagerWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_manager_work", 
            ExistingPeriodicWorkPolicy.KEEP, 
            periodicRequest
        )

        
        val immediateRequest = OneTimeWorkRequestBuilder<DailyManagerWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "immediate_setup_work",
            ExistingWorkPolicy.KEEP,
            immediateRequest
        )
    }
}

data class MainUiState(val gender: User.Gender = User.Gender.BOY)
