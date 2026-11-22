@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.menna.nabatan_7asena.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.menna.nabatan_7asena.data.local.PrayerSettingsStore
import com.menna.nabatan_7asena.data.schedulers.AzkarScheduler
import com.menna.nabatan_7asena.data.schedulers.PrayersScheduler
import com.menna.nabatan_7asena.data.schedulers.QuranScheduler
import com.menna.nabatan_7asena.domain.GetPrayerTimesUseCase
import com.menna.nabatan_7asena.domain.LocationTracker
import com.menna.nabatan_7asena.domain.entity.toPrayerPlace
import com.menna.nabatan_7asena.platform.AppClock
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@HiltWorker
class DailyManagerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val locationTracker: LocationTracker,
    private val prayersScheduler: PrayersScheduler,
    private val azkarScheduler: AzkarScheduler,
    private val quranScheduler: QuranScheduler
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        if (PrayerSettingsStore.state.value.selectedCity == null) {
            withTimeoutOrNull(12_000) { locationTracker.getCurrentLocation() }
                ?.toPrayerPlace()?.let(PrayerSettingsStore::saveGps)
        }
        scheduleCurrentLocation()
    } catch (e: CancellationException) { throw e }
    catch (e: Exception) {
        Log.e("DailyManagerWorker", "تعذر تحديث مواقيت الصلاة", e)
        Result.retry()
    }

    private suspend fun scheduleCurrentLocation(): Result {
        val request = PrayerSettingsStore.state.value.request ?: return Result.success()
        val date = Clock.System.now().toLocalDateTime(TimeZone.of(request.place.timeZoneId)).date
        val times = getPrayerTimesUseCase.at(request, AppClock.dateString(date)) ?: return Result.retry()
        if (request != PrayerSettingsStore.state.value.request || isStopped) return Result.retry()
        prayersScheduler.scheduleDailyPrayers(times, request.place.timeZoneId)
        azkarScheduler.scheduleFixedAzkar(times.fajr, request.place.timeZoneId)
        quranScheduler.scheduleWerd(times, request.place.timeZoneId)
        return Result.success()
    }
}
