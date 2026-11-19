package com.menna.nabatan_7asena.data.mappers

import com.menna.nabatan_7asena.data.local.room.DailyPlanEntity
import com.menna.nabatan_7asena.data.remote.dto.DailyPlanDto
import org.junit.Assert.*
import org.junit.Test

class RetainedDailyContentTest {
    @Test fun oldCachedPlansStillProvideWisdomQuranAndRiddle() {
        val cached = DailyPlanEntity(5, "Retired mission", "Wisdom", "Dhuhr", "Asr", "Maghrib",
            "Question", "Answer", "Answer,Other")
        val content = cached.toDomain()
        assertEquals("Wisdom", content.wisdom)
        assertEquals("Dhuhr", content.quranWerd.dhuhr)
        assertEquals("Asr", content.quranWerd.asr)
        assertEquals("Maghrib", content.quranWerd.maghrib)
        assertEquals("Question", content.riddle?.question)
        assertTrue(content.riddle!!.isCorrectAnswer("Answer"))
    }

    @Test fun newlyCachedContentPreservesOtherFieldsAndDoesNotStoreAMission() {
        val remote = DailyPlanDto(wisdom = "Wisdom", quranDhuhr = "Dhuhr", quranAsr = "Asr",
            quranMaghrib = "Maghrib", riddleQuestion = "Question", riddleAnswer = "Answer",
            riddleOptions = "Answer,Other")
        val cached = remote.toEntity(5)
        assertEquals("", cached.challenge)
        assertEquals("Wisdom", cached.wisdom)
        assertEquals("Maghrib", cached.quranMaghrib)
        assertEquals("Answer", cached.riddleAnswer)
    }
}
