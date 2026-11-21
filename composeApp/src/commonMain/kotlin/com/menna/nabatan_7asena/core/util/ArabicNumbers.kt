package com.menna.nabatan_7asena.core.util

fun arabicIndic(value: Int): String = value.toString().map { if (it in '0'..'9') '٠' + (it - '0') else it }.joinToString("")
fun ayahMarker(number: Int): String = "﴿${arabicIndic(number)}﴾"

