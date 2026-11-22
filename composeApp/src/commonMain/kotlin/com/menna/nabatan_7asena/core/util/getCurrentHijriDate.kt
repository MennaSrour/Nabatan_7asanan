package com.menna.nabatan_7asena.core.util

expect object DateUtils {
    fun getCurrentHijriDate(offsetDays: Long = HijriCalendar.offset.toLong()): String
    fun currentHijriParts(offsetDays: Int): HijriDate
}

data class HijriDate(val day: Int, val month: Int, val year: Int) {
    init { require(day in 1..30 && month in 1..12 && year > 0) }
}

object HijriCalendar {
    val offset get() = com.menna.nabatan_7asena.data.local.PrayerSettingsStore.state.value.hijriDateOffset
    fun today() = DateUtils.currentHijriParts(offset)
}

enum class IslamicSeason { ORDINARY, RAMADAN, EID_AL_FITR, DHUL_HIJJAH, ARAFAH, EID_AL_ADHA }

data class SeasonalCalendar(val season: IslamicSeason, val ramadanDay: Int?)

object SeasonalThemeManager {
    fun current() = forDate(HijriCalendar.today())
    fun forDate(date: HijriDate): SeasonalCalendar = SeasonalCalendar(
        when {
            date.month == 9 -> IslamicSeason.RAMADAN
            date.month == 10 && date.day <= 3 -> IslamicSeason.EID_AL_FITR
            date.month == 12 && date.day == 9 -> IslamicSeason.ARAFAH
            date.month == 12 && date.day in 10..13 -> IslamicSeason.EID_AL_ADHA
            date.month == 12 && date.day <= 8 -> IslamicSeason.DHUL_HIJJAH
            else -> IslamicSeason.ORDINARY
        },
        date.day.takeIf { date.month == 9 }
    )
}

object RamadanCalendar {
    fun currentDay(): Int? = SeasonalThemeManager.current().ramadanDay
}
