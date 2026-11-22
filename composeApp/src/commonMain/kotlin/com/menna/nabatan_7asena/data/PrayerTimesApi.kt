package com.menna.nabatan_7asena.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PrayerTimesApi(private val client: HttpClient = createHttpClient()) {
    suspend fun getPrayerTimes(request: com.menna.nabatan_7asena.domain.entity.PrayerRequest, date: String): PrayerApiResponse =
        client.get("https://api.aladhan.com/v1/timings/$date") {
            parameter("latitude", request.place.latitude)
            parameter("longitude", request.place.longitude)
            parameter("timezonestring", request.place.timeZoneId)
            parameter("method", request.method.apiId)
        }.body()

    suspend fun getPrayerTimesByCity(date: String, city: String, country: String, method: Int = com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod.forCountry(country).apiId): PrayerApiResponse =
        client.get("https://api.aladhan.com/v1/timingsByCity/$date") {
            parameter("city", city)
            parameter("country", country)
            parameter("method", method)
        }.body()
}
