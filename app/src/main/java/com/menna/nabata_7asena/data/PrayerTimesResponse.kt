package com.menna.nabata_7asena.data

import com.google.gson.annotations.SerializedName


data class PrayerApiResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: PrayerData
)


data class PrayerData(
    @SerializedName("timings")
    val timings: Timings,
    @SerializedName("date")
    val date: Date
)


data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class Date(
    @SerializedName("hijri") val hijri: Hijri
)

data class Hijri(
    @SerializedName("date") val date: String
)