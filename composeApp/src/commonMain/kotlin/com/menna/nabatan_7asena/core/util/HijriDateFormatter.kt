package com.menna.nabatan_7asena.core.util

private val HijriMonthNames = listOf(
    "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
    "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
)

internal fun formatHijriDate(day: Int, month: Int, year: Int): String {
    require(day in 1..30 && month in 1..12 && year > 0) { "Invalid Hijri date" }
    return "$day ${HijriMonthNames[month - 1]} $year"
}
