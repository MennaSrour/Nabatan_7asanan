package com.menna.nabata_7asena.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerTimesApi {
    @GET("timingsByCity/{date}")
    suspend fun getPrayerTimesByCity(
        @Path("date") date: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 5
    ): PrayerApiResponse 
}