package com.menna.nabata_7asena.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.menna.nabata_7asena.domain.GetPrayerTimesUseCase
import com.menna.nabata_7asena.domain.LocationTracker
import com.menna.nabata_7asena.domain.schedulers.AzkarScheduler
import com.menna.nabata_7asena.domain.schedulers.PrayersScheduler
import com.menna.nabata_7asena.domain.schedulers.QuranScheduler
import com.menna.nabata_7asena.domain.schedulers.TaskScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class DailyManagerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val locationTracker: LocationTracker,   
    private val prayersScheduler: PrayersScheduler, 
    private val azkarScheduler: AzkarScheduler,     
    private val quranScheduler: QuranScheduler,     
    private val taskScheduler: TaskScheduler     
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DailyManagerWorker", "بدء العمل اليومي لجلب المواقيت والجدولة...")

            
            
            val location = locationTracker.getCurrentLocation()
            val city = location?.city ?: "Cairo"
            val country = location?.country ?: "Egypt"

            
            val todayDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date())

            Log.d("DailyManagerWorker", "جاري جلب المواقيت لـ: $city, $country - تاريخ: $todayDate")

            
            val prayerTimes = getPrayerTimesUseCase(
                city = city,
                country = country,
                date = todayDate
            )

            if (prayerTimes != null) {
                Log.d("DailyManagerWorker", "تم جلب المواقيت بنجاح. جاري التوزيع على الجدولة...")

                

                
                prayersScheduler.scheduleDailyPrayers(prayerTimes)

                
                
                azkarScheduler.scheduleFixedAzkar(prayerTimes.fajr)

                
                quranScheduler.scheduleWerd(prayerTimes)

                
                taskScheduler.scheduleDailyChallenge()

                Log.d("DailyManagerWorker", "تمت الجدولة بنجاح لكل المهام ✅")
                Result.success()
            } else {
                Log.e("DailyManagerWorker", "فشل جلب المواقيت (Null response)")
                Result.retry() 
            }

        } catch (e: Exception) {
            Log.e("DailyManagerWorker", "خطأ غير متوقع في الـ Worker: ${e.message}")
            Result.retry()
        }
    }
}