package com.menna.nabatan_7asena.platform

import androidx.work.*
import com.menna.nabatan_7asena.core.worker.DailyManagerWorker
import java.util.concurrent.TimeUnit

class AndroidScheduling : SchedulingPlatform {
    private val work get() = WorkManager.getInstance(AndroidPlatform.context)
    override fun enableDailyAutoScheduling(replace: Boolean) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodic = PeriodicWorkRequestBuilder<DailyManagerWorker>(24, TimeUnit.HOURS).setConstraints(constraints).build()
        work.enqueueUniquePeriodicWork("daily_manager_work", if (replace) ExistingPeriodicWorkPolicy.UPDATE else ExistingPeriodicWorkPolicy.KEEP, periodic)
        if (!replace) scheduleImmediately()
    }
    override fun scheduleImmediately() {
        val request = OneTimeWorkRequestBuilder<DailyManagerWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .addTag("immediate_setup_work").build()
        work.enqueueUniqueWork("immediate_setup_work", ExistingWorkPolicy.KEEP, request)
    }
    override fun cancelDailyAutoScheduling() { work.cancelUniqueWork("daily_manager_work") }
}
