package com.menna.nabatan_7asena.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.menna.nabatan_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabatan_7asena.data.remote.dto.QuizQuestionDto
import com.menna.nabatan_7asena.data.remote.dto.QuizScoreDto
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getDailyPlan(dayId: Int): DailyPlanDto? {
        try {
            Log.d("DailyPlanDebug", "جاري البحث عن خطة يوم رقم: $dayId")

            val snapshot = firestore.collection("daily_plans")
                .document(dayId.toString())
                .get()
                .await()

            if (snapshot.exists()) {
                Log.d("DailyPlanDebug", "تم العثور على الخطة! البيانات: ${snapshot.data}")
                return snapshot.toObject(DailyPlanDto::class.java)
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
                .orderBy("totalStars", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            return snapshot.toObjects(UserRankDto::class.java)
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
                .await()
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
                    .await()

                if (snapshot.exists()) {
                    val dto = snapshot.toObject(DailyPlanDto::class.java)
                    if (dto != null) plans.add(dto)
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
            firestore.collection("leaderboard").document(userId).delete().await()
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
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
            return snapshot.toObjects(QuizQuestionDto::class.java)
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
                .await()
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
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .await()
            return snapshot.toObjects(QuizScoreDto::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "حدث خطأ في جلب أبطال أسبوع $weekId", e)
            throw e
        }
    }
}