package com.menna.nabata_7asena.data

import com.menna.nabata_7asena.domain.entity.PrayerTimes


fun PrayerApiResponse.toEntity(): PrayerTimes = PrayerTimes(
    fajr = this.data.timings.fajr,
    dhuhr = this.data.timings.dhuhr,
    asr = this.data.timings.asr,
    maghrib = this.data.timings.maghrib,
    isha = this.data.timings.isha
)