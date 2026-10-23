package com.menna.nabatan_7asena.domain.util

import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object QuizWeek {
    private val CAIRO_ZONE = TimeZone.of("Africa/Cairo")
    private const val STUDY_POOL_SIZE = 20
    private const val COMPETITION_SIZE = 5

    fun today(): LocalDate {
        return Clock.System.now().toLocalDateTime(CAIRO_ZONE).date
    }

    fun weekIdFor(date: LocalDate): String {

        val isoNum = date.dayOfWeek.isoDayNumber
        val daysSinceSaturday = (isoNum - 6 + 7) % 7
        return date.minus(daysSinceSaturday, DateTimeUnit.DAY).toString()
    }

    fun currentWeekId(): String = weekIdFor(today())

    fun isCompetitionDay(date: LocalDate = today()): Boolean =
        date.dayOfWeek.isoDayNumber == 5
    fun mostRecentCompetitionWeekId(date: LocalDate = today()): String {
        var tempDate = date
        while (tempDate.dayOfWeek.isoDayNumber != 5) {
            tempDate = tempDate.minus(1, DateTimeUnit.DAY)
        }
        return weekIdFor(tempDate)
    }

    fun studyQuestionsFor(allQuestions: List<QuizQuestion>, weekId: String): List<QuizQuestion> {
        if (allQuestions.isEmpty()) return emptyList()
        val ordered = allQuestions.sortedBy { it.id }
        val shuffled = ordered.shuffled(Random(weekId.hashCode()))
        return shuffled.take(STUDY_POOL_SIZE.coerceAtMost(shuffled.size))
    }

    fun competitionQuestionsFor(studyQuestions: List<QuizQuestion>, weekId: String): List<QuizQuestion> {
        if (studyQuestions.isEmpty()) return emptyList()
        val shuffled = studyQuestions.shuffled(Random((weekId + "_comp").hashCode()))
        return shuffled.take(COMPETITION_SIZE.coerceAtMost(shuffled.size))
    }

    fun shuffledForSession(questions: List<QuizQuestion>): List<QuizQuestion> =
        questions.shuffled()
}