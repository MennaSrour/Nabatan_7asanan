package com.menna.nabatan_7asena.data.remote

import com.menna.nabatan_7asena.platform.Log
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.Source
import com.menna.nabatan_7asena.data.remote.dto.SpeedQuestionDto
import com.menna.nabatan_7asena.data.remote.dto.VerseBuilderDto
import com.menna.nabatan_7asena.data.remote.dto.WheelClaimDto
import com.menna.nabatan_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabatan_7asena.data.remote.dto.QuizQuestionDto
import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto

class FirebaseDataSource constructor(
    private val firestoreProvider: () -> FirebaseFirestore = { Firebase.firestore }
) : OfficialQuizRemote {
    private val firestore get(): FirebaseFirestore {
        check(FirebaseAvailability.isConfigured) { "Firebase is not configured for this host" }
        return firestoreProvider()
    }
    suspend fun getDailyPlan(dayId: Int): DailyPlanDto? {
        try {
            Log.d("DailyPlanDebug", "جاري البحث عن خطة يوم رقم: $dayId")

            val snapshot = firestore.collection("daily_plans")
                .document(dayId.toString())
                .get()

            if (snapshot.exists) {
                Log.d("DailyPlanDebug", "تم العثور على الخطة ليوم: $dayId")
                return snapshot.data<DailyPlanDto>()
            }
            Log.w("DailyPlanDebug", "لا توجد خطة متاحة لليوم: $dayId")
            return null
        } catch (e: Exception) {
            Log.e("DailyPlanDebug", "حدث خطأ في جلب خطة اليوم", e)
            throw e
        }
    }

    suspend fun getLeaderboard(): List<UserRankDto> {
        try {
            val snapshot = firestore.collection("leaderboard")
                .orderBy("totalStars", Direction.DESCENDING)
                .limit(50)
                .get()
            return snapshot.documents.map { it.data<UserRankDto>() }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في جلب لوحة المتصدرين", e)
            throw e
        }
    }

    suspend fun getVerseBuilderItems(): List<VerseBuilderDto> =
        firestore.collection("verse_builder_items").limit(100).get().documents
            .mapNotNull { runCatching { it.data<VerseBuilderDto>() }.getOrNull() }

    suspend fun getSpeedChallengeQuestions(): List<SpeedQuestionDto> =
        firestore.collection("speed_challenge_questions").limit(100).get().documents
            .mapNotNull { runCatching { it.data<SpeedQuestionDto>() }.getOrNull() }

    private fun wheelClaim(profileId: String, day: String) =
        firestore.collection("daily_reward_profiles").document(profileId).collection("spins").document(day)

    suspend fun getWheelClaim(profileId: String, day: String): WheelClaimDto? {
        val snapshot = wheelClaim(profileId, day).get(Source.SERVER)
        return if (snapshot.exists) snapshot.data<WheelClaimDto>() else null
    }

    suspend fun claimWheel(profileId: String, day: String, claim: WheelClaimDto): WheelClaimDto {
        val document = wheelClaim(profileId, day)
        return firestore.runTransaction {
            val existing = get(document)
            if (existing.exists) existing.data<WheelClaimDto>() else {
                set(document, claim)
                claim
            }
        }
    }

    suspend fun backupGarden(profileId: String, deviceId: String, progress: com.menna.nabatan_7asena.domain.entity.GardenProgress) {
        firestore.collection("gardens").document(profileId).collection("devices").document(deviceId).set(progress)
    }

    suspend fun getUserRank(totalStars: Int): Long {
        val higherScores = firestore.collection("leaderboard")
            .where { "totalStars" greaterThan totalStars }
            .serverCount()
        check(higherScores >= 0 && higherScores < Long.MAX_VALUE) { "Invalid leaderboard count" }
        return higherScores + 1
    }

    suspend fun uploadUserScore(dto: UserRankDto) {
        try {
            firestore.collection("leaderboard")
                .document(dto.userId)
                .set(dto)
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في رفع نتيجة المستخدم ${dto.userId}", e)
            throw e
        }
    }

    suspend fun getFuturePlans(startDayId: Int, limit: Int = 7): List<DailyPlanDto> {
        try {
            val plans = mutableListOf<DailyPlanDto>()
            for (i in 0 until limit) {
                val nextDayId = startDayId + 1 + i
                val snapshot = firestore.collection("daily_plans")
                    .document(nextDayId.toString())
                    .get()

                if (snapshot.exists) {
                    plans.add(snapshot.data<DailyPlanDto>())
                }
            }
            return plans
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في جلب الخطط المستقبلية", e)
            throw e
        }
    }

    suspend fun deleteUserScore(userId: String) {
        try {
            firestore.collection("leaderboard").document(userId).delete()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في حذف نتيجة المستخدم $userId", e)
            throw e
        }
    }

    // ─────────────────────────────────────────────────────────
    // مسابقة المعلومات الدينية الأسبوعية
    // ─────────────────────────────────────────────────────────

    /** كل بنك الأسئلة (60، 100، أيًا كان)، مرتب بـ order — منطق اختيار الأسبوع نفسه محلي في QuizWeek */
    suspend fun getQuizQuestions(): List<QuizQuestionDto> {
        try {
            val snapshot = firestore.collection("quiz_questions")
                .orderBy("order", Direction.ASCENDING)
                .get()
            return snapshot.documents.map { it.data<QuizQuestionDto>() }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في جلب أسئلة المسابقة", e)
            throw e
        }
    }

    /** رفع نتيجة طفل لمسابقة أسبوع معيّن — set (مش add) عشان لو حاول يرسل تاني يستبدل مش يتكرر */
    override suspend fun getOfficialQuizAttempt(weekId: String, userId: String): QuizScoreDto? {
        val scores = firestore.collection("quiz_scores").document(weekId).collection("entries").document(userId)
        val score = scores.get(dev.gitlive.firebase.firestore.Source.SERVER)
        if (score.exists) return score.data<QuizScoreDto>()
        val attempt = firestore.collection("quiz_attempts").document(weekId).collection("entries").document(userId)
            .get(dev.gitlive.firebase.firestore.Source.SERVER)
        return if (attempt.exists) attempt.data<QuizScoreDto>() else null
    }

    override suspend fun reserveOfficialQuizAttempt(weekId: String, dto: QuizScoreDto): Pair<QuizScoreDto, Boolean> {
        val reservation = firestore.collection("quiz_attempts").document(weekId).collection("entries").document(dto.userId)
        val score = firestore.collection("quiz_scores").document(weekId).collection("entries").document(dto.userId)
        return firestore.runTransaction {
            val savedScore = get(score)
            val savedAttempt = get(reservation)
            when {
                savedScore.exists -> savedScore.data<QuizScoreDto>() to false
                savedAttempt.exists -> savedAttempt.data<QuizScoreDto>() to false
                else -> { set(reservation, dto); dto to true }
            }
        }
    }

    override suspend fun finishOfficialQuizAttempt(weekId: String, userId: String, token: String, score: Int): QuizScoreDto {
        val reservation = firestore.collection("quiz_attempts").document(weekId).collection("entries").document(userId)
        val result = firestore.collection("quiz_scores").document(weekId).collection("entries").document(userId)
        return firestore.runTransaction {
            val existing = get(result)
            val started = get(reservation)
            if (existing.exists) existing.data<QuizScoreDto>()
            else {
                check(started.exists) { "Official attempt was not reserved" }
                val saved = started.data<QuizScoreDto>()
                check(saved.token == token && token.isNotBlank()) { "Official attempt does not match" }
                val finished = saved.copy(score = score, completed = true)
                set(result, finished)
                set(reservation, finished)
                finished
            }
        }
    }

    /** أعلى 3 أطفال في أسبوع معيّن — دول اللي بيظهروا في البوديوم */
    suspend fun getQuizWeekWinners(weekId: String): List<QuizScoreDto> {
        try {
            val snapshot = firestore.collection("quiz_scores")
                .document(weekId)
                .collection("entries")
                .orderBy("score", Direction.DESCENDING)
                .limit(3)
                .get()
            return snapshot.documents.map { it.data<QuizScoreDto>() }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في جلب أبطال أسبوع $weekId", e)
            throw e
        }
    }
}
/** The iOS host sets this before composing; missing configuration remains a recoverable data error. */
object FirebaseAvailability { var isConfigured: Boolean = true }
