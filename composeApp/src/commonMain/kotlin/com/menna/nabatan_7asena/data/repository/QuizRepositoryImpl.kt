package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.QuizRepository
import com.menna.nabatan_7asena.domain.util.QuizWeek

class QuizRepositoryImpl constructor(
    private val remote: FirebaseDataSource,
    private val official: com.menna.nabatan_7asena.domain.repository.OfficialQuizRepository
) : QuizRepository {

    private var cachedAllQuestions: List<QuizQuestion>? = null

    private suspend fun allQuestions(): List<QuizQuestion> {
        cachedAllQuestions?.let { return it }
        val fetched = remote.getQuizQuestions().map { dto ->
            QuizQuestion(
                id = dto.order,
                text = dto.text,
                choices = dto.choices,
                correctIndex = dto.correctIndex
            )
        }
        cachedAllQuestions = fetched
        return fetched
    }

    override suspend fun getWeeklyStudyQuestions(): List<QuizQuestion> {
        val weekId = QuizWeek.currentWeekId()
        return QuizWeek.studyQuestionsFor(allQuestions(), weekId)
    }

    override suspend fun getWeeklyCompetitionQuestions(): List<QuizQuestion> {
        val weekId = QuizWeek.currentWeekId()
        val study = QuizWeek.studyQuestionsFor(allQuestions(), weekId)
        return QuizWeek.competitionQuestionsFor(study, weekId)
    }

    override fun isCompetitionDayToday(): Boolean = QuizWeek.isCompetitionDay()

    override suspend fun submitCompetitionScore(score: Int) {
        val attempt = official.status() ?: error("No official attempt was reserved")
        official.finish(attempt, score)
    }

    override suspend fun getRecentWeekWinners(): List<QuizWinner> {
        val weekId = QuizWeek.mostRecentCompetitionWeekId()
        return remote.getQuizWeekWinners(weekId).map {
            QuizWinner(
                userId = it.userId,
                name = it.name,
                gender = when (it.gender.uppercase()) {
                    "FEMALE", "أنثى", "F" -> User.Gender.GIRL
                    else -> User.Gender.BOY
                },
                avatarIndex = it.avatarIndex,
                score = it.score
            )
        }
    }
}