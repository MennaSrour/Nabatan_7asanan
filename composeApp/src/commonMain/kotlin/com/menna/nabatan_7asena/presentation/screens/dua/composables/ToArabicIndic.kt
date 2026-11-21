package com.menna.nabatan_7asena.presentation.screens.dua.composables

fun Int.toArabicIndic(): String =
toString().map { ch -> arabicIndicNumerals.getOrElse(ch - '0') { ch } }.joinToString("")
