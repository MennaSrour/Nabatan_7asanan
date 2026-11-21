package com.menna.nabatan_7asena.domain.util

/** The source encodes the final Ayah-number glyph as FC00 + number - 1, not Quran text. */
fun uthmaniText(raw: String, number: Int): String {
    val text = raw.replace("\\xa0", " ").trimEnd()
    val withoutMarker = if (text.lastOrNull() == (0xFC00 + number - 1).toChar()) text.dropLast(1) else text
    return withoutMarker.trim()
}

