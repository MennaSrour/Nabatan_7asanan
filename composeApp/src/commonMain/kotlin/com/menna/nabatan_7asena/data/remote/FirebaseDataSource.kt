package com.menna.nabatan_7asena.data.remote

import com.menna.nabatan_7asena.platform.Log
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Direction
import com.menna.nabatan_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabatan_7asena.data.remote.dto.QuizQuestionDto
import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto

class FirebaseDataSource constructor(
    private val firestoreProvider: () -> FirebaseFirestore = { Firebase.firestore }
) {
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
    suspend fun submitQuizScore(weekId: String, dto: QuizScoreDto) {
        try {
            firestore.collection("quiz_scores")
                .document(weekId)
                .collection("entries")
                .document(dto.userId)
                .set(dto)
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في رفع نتيجة المسابقة لـ ${dto.userId}", e)
            throw e
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
