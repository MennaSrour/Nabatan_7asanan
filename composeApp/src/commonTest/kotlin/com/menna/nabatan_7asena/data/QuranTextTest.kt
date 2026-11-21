package com.menna.nabatan_7asena.data

import com.menna.nabatan_7asena.domain.util.uthmaniText
import com.menna.nabatan_7asena.core.util.ayahMarker
import kotlin.test.*

class QuranTextTest {
    @Test fun decodesOnlyTheLegacyEndMarkerAndKeepsEveryRecitationMark() {
        val text = "الٓمٓ ۛ ۖ ۗ ٞ"
        assertEquals(text, uthmaniText(text + "\\xa0" + 0xFC00.toChar(), 1))
        assertEquals(text, uthmaniText(text, 1))
        assertEquals("﴿٢٨٦﴾", ayahMarker(286))
    }
}
