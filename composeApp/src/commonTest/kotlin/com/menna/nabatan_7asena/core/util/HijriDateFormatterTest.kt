package com.menna.nabatan_7asena.core.util

import kotlin.test.*

class HijriDateFormatterTest {
    @Test fun usesTheFullArabicMonthNamesInCalendarOrder() {
        val names = listOf("محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة")
        names.forEachIndexed { index, name -> assertEquals("11 $name 1448", formatHijriDate(11, index + 1, 1448)) }
    }

    @Test fun acceptsMonthAndYearBoundaries() {
        assertEquals("30 ذو الحجة 1448", formatHijriDate(30, 12, 1448))
        assertEquals("1 محرم 1449", formatHijriDate(1, 1, 1449))
    }

    @Test fun rejectsInvalidComponentsInsteadOfDisplayingAMisleadingDate() {
        assertFailsWith<IllegalArgumentException> { formatHijriDate(0, 6, 1448) }
        assertFailsWith<IllegalArgumentException> { formatHijriDate(31, 6, 1448) }
        assertFailsWith<IllegalArgumentException> { formatHijriDate(11, 0, 1448) }
        assertFailsWith<IllegalArgumentException> { formatHijriDate(11, 13, 1448) }
        assertFailsWith<IllegalArgumentException> { formatHijriDate(11, 6, 0) }
    }
}
