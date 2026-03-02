package com.menna.nabata_7asena.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.menna.nabata_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabata_7asena.data.remote.dto.UserRankDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
}