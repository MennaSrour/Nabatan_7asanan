package com.menna.nabatan_7asena.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PrayerTimesApi(private val client: HttpClient = createHttpClient()) {
    suspend fun getPrayerTimesByCity(date: String, city: String, country: String, method: Int = 5): PrayerApiResponse =
        client.get("https://api.aladhan.com/v1/timingsByCity/$date") {
            parameter("city", city)
            parameter("country", country)
            parameter("method", method)
        }.body()
}
