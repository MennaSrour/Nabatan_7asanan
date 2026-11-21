package com.menna.nabatan_7asena.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class PrayerApiResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("status")
    val status: String,
    @SerialName("data")
    val data: PrayerData
)


@Serializable
data class PrayerData(
    @SerialName("timings")
    val timings: Timings,
    @SerialName("date")
    val date: Date
)


@Serializable
data class Timings(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)

@Serializable
data class Date(
    @SerialName("hijri") val hijri: Hijri
)

@Serializable
data class Hijri(
    @SerialName("date") val date: String
)