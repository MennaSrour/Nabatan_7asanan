package com.menna.nabatan_7asena.presentation.screens.home.composables

import kotlin.test.*

class OriginalPrayerIconTest {
    @Test fun countdownSharesTheFiveOriginalPrayerAssetNames() {
        assertEquals("fajr", prayerIconRes("الفجر"))
        assertEquals("zuhr_sun", prayerIconRes("الظهر"))
        assertEquals("asr_sun", prayerIconRes("العصر"))
        assertEquals("maghrib", prayerIconRes("المغرب"))
        assertEquals("ishaa", prayerIconRes("العشاء"))
    }
}
