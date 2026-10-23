package com.menna.nabatan_7asena.presentation.screens.dua.composables

internal fun Int.toArabicIndic(): String =
toString().map { ch -> arabicIndicNumerals.getOrElse(ch - '0') { ch } }.joinToString("")
