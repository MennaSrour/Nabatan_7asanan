package com.menna.nabatan_7asena.core.alarm

object AlarmConstants {
    // الصلوات (PrayersScheduler)
    const val ID_FAJR = 1001
    const val ID_DHUHR = 1002
    const val ID_ASR = 1003
    const val ID_MAGHRIB = 1004
    const val ID_ISHA = 1005

    // الأذكار (AzkarScheduler)
    const val ID_AZKAR_1 = 2001
    const val ID_AZKAR_2 = 2002
    const val ID_AZKAR_3 = 2003
    const val ID_AZKAR_4 = 2004
    const val ID_AZKAR_5 = 2005
    const val ID_AZKAR_6 = 2006
    const val ID_AZKAR_7 = 2007
    const val ID_AZKAR_MORNING = 2008 // ID مستقل لأذكار الصباح بعد الفجر (كان متصادم مع 2005 قبل كده)

    // القرآن (QuranScheduler)
    const val ID_QURAN_DHUHR = 3001
    const val ID_QURAN_ASR = 3002
    const val ID_QURAN_MAGHRIB = 3003

    // التحدي اليومي (TaskScheduler)
    const val ID_DAILY_CHALLENGE = 4001
}