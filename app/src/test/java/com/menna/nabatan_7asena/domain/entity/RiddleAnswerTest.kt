package com.menna.nabatan_7asena.domain.entity

import org.junit.Assert.*
import org.junit.Test

class RiddleAnswerTest {
    @Test fun surroundingSpacesDoNotMakeTheCorrectOptionWrong() {
        val riddle = DailyContent.Riddle("سؤال", " الإجابة ", listOf("الإجابة", "أخرى"))
        assertTrue(riddle.isCorrectAnswer("  الإجابة  "))
    }
    @Test fun differentAnswerIsStillWrong() {
        val riddle = DailyContent.Riddle("سؤال", "الإجابة", listOf("الإجابة", "أخرى"))
        assertFalse(riddle.isCorrectAnswer("أخرى"))
    }
    @Test fun missingAnswerCannotBeAcceptedAsCorrect() {
        val riddle = DailyContent.Riddle("سؤال", "  ", listOf(""))
        assertFalse(riddle.isCorrectAnswer(""))
    }
}
