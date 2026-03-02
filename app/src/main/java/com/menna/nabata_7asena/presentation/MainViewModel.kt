package com.menna.nabata_7asena.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.menna.nabata_7asena.core.worker.DailyManagerWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context 
) : ViewModel() {

    
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