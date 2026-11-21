package com.menna.nabatan_7asena.platform

import com.menna.nabatan_7asena.resources.AppResources as R

/** Old Android resource IDs vary by release; recover pending alarm sounds from their content. */
fun alarmSoundResource(resource: Int, title: String, message: String, destination: String?): Int {
    if (resource < 0) return resource
    return when {
        destination == "quran" -> R.raw.werd
        title == "الصلاة" -> prayerSound(message)
        title == "أذكار الصباح" -> R.raw.yala_zekr
        destination == "sebha" -> zekrSound(message)
        else -> R.raw.sound_normal
    }
}

private fun prayerSound(message: String): Int = when {
    "الفجر" in message -> R.raw.fajr
    "الظهر" in message -> R.raw.zuhr
    "العصر" in message -> R.raw.asr
    "العشاء" in message -> R.raw.ishaa
    else -> R.raw.sound_normal
}

private fun zekrSound(message: String): Int = when (message) {
    "لا إله إلا الله" -> R.raw.la_ilah_ila_allah
    "استغفر الله العظيم" -> R.raw.yala_zekr
    "الله أكبر" -> R.raw.allahu_akbar
    "سبحان الله وبحمده" -> R.raw.subhan_allah_wa_behamdeh
    "الحمدلله" -> R.raw.alhamdulillah
    "سبحان الله" -> R.raw.subhan_allah
    "لا حول ولا قوة إلا بالله" -> R.raw.la_hawla
    else -> R.raw.sound_normal
}
