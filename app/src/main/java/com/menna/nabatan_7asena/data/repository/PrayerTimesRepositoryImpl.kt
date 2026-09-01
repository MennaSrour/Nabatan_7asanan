package com.menna.nabatan_7asena.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.menna.nabatan_7asena.data.PrayerTimesApi
import com.menna.nabatan_7asena.data.toEntity
import com.menna.nabatan_7asena.domain.PrayerTimesRepository
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * بيجيب مواقيت الصلاة من النت مرة واحدة بس لكل (يوم + مدينة)،
 * وبعد كده بيرجّع نفس النتيجة من الكاش المحلي طول اليوم من غير أي Request جديد.
 *
 * لو يوم جديد بدأ والنت مقطوع وقت المحاولة، بدل ما نرجّع فشل (وتختفي الصلوات
 * من الهوم بالكامل)، بنرجع آخر مواقيت اتحفظت لنفس المدينة والدولة حتى لو
 * كانت بتاعة يوم سابق — الفرق في المواقيت بين يوم وتاني عادة دقايق قليلة بس،
 * فده أفضل بكتير من إننا منعرضش حاجة خالص.
 */
class PrayerTimesRepositoryImpl @Inject constructor(
    private val api: PrayerTimesApi,
    @ApplicationContext private val context: Context
) : PrayerTimesRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    override suspend fun getPrayerTimes(
        city: String,
        country: String,
        date: String
    ): PrayerTimes {
        // بصمة الكاش الدقيقة: مرتبطة باليوم والمكان، لو أي منهم اتغير محتاجين نسخة جديدة
        val cacheSignature = "$date|$city|$country"
        // بصمة المكان بس، بنستخدمها للـ fallback لو النت وقع وقت يوم جديد
        val locationKey = "$city|$country"

        // 1) فيه كاش مطابق تمامًا لليوم والمكان؟ استخدمه على طول
        readExactCache(cacheSignature)?.let { return it }

        // 2) مفيش كاش صالح لليوم ده → نحاول نجيب نسخة جديدة من النت
        return try {
            val fresh = api.getPrayerTimesByCity(
                city = city,
                country = country,
                date = date
            ).toEntity()

            saveToCache(cacheSignature, locationKey, fresh)
            fresh
        } catch (e: Exception) {
            Log.e("PrayerTimesRepo", "Failed to fetch fresh prayer times: ${e.message}")

            // 3) مفيش نت؟ نرجع آخر نسخة معروفة لنفس المدينة/الدولة حتى لو قديمة شوية،
            // أفضل بكتير من إننا نرجع فشل ونخلي الصلوات تختفي من الهوم
            readStaleCacheForLocation(locationKey)?.also {
                Log.w("PrayerTimesRepo", "No internet — falling back to last known prayer times for $locationKey")
            } ?: throw e // مفيش أي كاش خالص من قبل (أول مرة أوفلاين) → نرمي الخطأ زي ما هو
        }
    }

    private fun readExactCache(cacheSignature: String): PrayerTimes? {
        val savedSignature = prefs.getString(KEY_CACHE_SIGNATURE, null)
        val savedJson = prefs.getString(KEY_CACHE_DATA, null)

        if (savedSignature != cacheSignature || savedJson == null) return null
        return parseCache(savedJson)
    }

    private fun readStaleCacheForLocation(locationKey: String): PrayerTimes? {
        val savedLocation = prefs.getString(KEY_CACHE_LOCATION, null)
        val savedJson = prefs.getString(KEY_CACHE_DATA, null)

        // مهم: منرجعش مواقيت مدينة تانية بالغلط — لازم المكان يتطابق حتى لو التاريخ مختلف
        if (savedLocation != locationKey || savedJson == null) return null
        return parseCache(savedJson)
    }

    private fun parseCache(json: String): PrayerTimes? {
        return try {
            gson.fromJson(json, PrayerTimes::class.java)
        } catch (e: Exception) {
            // لو الكاش المحفوظ اتبعثر لأي سبب (تحديث للتطبيق غيّر شكل الكلاس مثلاً)
            // نتجاهله ونكمل بدل ما نكسر التطبيق
            Log.e("PrayerTimesCache", "Corrupted cache, ignoring: ${e.message}")
            null
        }
    }

    private fun saveToCache(cacheSignature: String, locationKey: String, data: PrayerTimes) {
        prefs.edit()
            .putString(KEY_CACHE_SIGNATURE, cacheSignature)
            .putString(KEY_CACHE_LOCATION, locationKey)
            .putString(KEY_CACHE_DATA, gson.toJson(data))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "prayer_times_cache"
        private const val KEY_CACHE_SIGNATURE = "cache_signature"
        private const val KEY_CACHE_LOCATION = "cache_location"
        private const val KEY_CACHE_DATA = "cache_data"
    }
}