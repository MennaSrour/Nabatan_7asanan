package com.menna.nabatan_7asena.data.remote

import com.menna.nabatan_7asena.platform.Log
import com.menna.nabatan_7asena.data.remote.dto.KidsShortDto
import com.menna.nabatan_7asena.domain.entity.KidsShort
import kotlinx.coroutines.CancellationException
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val authMutex = Mutex()

    internal suspend fun ownerUid(): String = authMutex.withLock {
        check(FirebaseAvailability.isConfigured) { "Firebase is not configured for this host" }
        val auth = Firebase.auth
        auth.currentUser?.uid ?: checkNotNull(auth.signInAnonymously().user).uid
    }

    internal suspend fun renewAnonymousOwner(expectedUid: String): String = authMutex.withLock {
        val auth = Firebase.auth
        val current = auth.currentUser
        if (current?.uid == expectedUid) auth.signOut()
        auth.currentUser?.uid ?: checkNotNull(auth.signInAnonymously().user).uid
    }

    internal suspend fun getKidsShorts(): List<KidsShort> =
        firestore.collection("kids_shorts")
            .where { "isActive" equalTo true }
            .get()
            .documents
            .mapNotNull { document ->
                try {
                    document.data<KidsShortDto>().toDomain(document.id)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.w("KidsShorts", "Skipping invalid clip document ${document.id}")
                    null
                }
            }
            .sortedWith(compareBy(KidsShort::order, KidsShort::id))
            .distinctBy(KidsShort::id)

    suspend fun getDailyPlan(dayId: Int): DailyPlanDto? {
        val snapshot = firestore.collection("daily_plans").document(dayId.toString()).get()
        return if (snapshot.exists) snapshot.data<DailyPlanDto>() else null
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

    internal suspend fun getGameQuestions(game: String): List<SpeedQuestionDto> =
        firestore.collection("game_questions").document(game).collection("items")
            .get(Source.SERVER).documents.mapNotNull { runCatching { it.data<SpeedQuestionDto>() }.getOrNull() }

    internal suspend fun getGameVerses(): List<VerseBuilderDto> =
        firestore.collection("game_questions").document("verse").collection("items")
            .get(Source.SERVER).documents.mapNotNull { runCatching { it.data<VerseBuilderDto>() }.getOrNull() }

    private suspend fun wheelClaim(profileId: String, day: String) =
        firestore.collection("users").document(ownerUid()).collection("daily_reward_profiles")
            .document(profileId).collection("spins").document(day)

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
        firestore.collection("gardens").document(ownerUid()).collection("devices").document("${profileId}_$deviceId").set(progress)
    }

    suspend fun backupRamadanDays(profileId: String, year: Int, completed: Set<Int>) {
        val uid = ownerUid()
        val store = firestore
        val days = store.collection("ramadan_calendars").document(uid)
            .collection("profiles").document(profileId).collection("days")
        val batch = store.batch()
        for (day in completed) {
            batch.set(days.document("${year}_$day"), mapOf("year" to year, "day" to day))
        }
        batch.commit()
    }

    suspend fun getUserRank(totalStars: Int): Long {
        val higherScores = firestore.collection("leaderboard")
            .where { "totalStars" greaterThan totalStars }
            .serverCount()
        check(higherScores >= 0 && higherScores < Long.MAX_VALUE) { "Invalid leaderboard count" }
        return higherScores + 1
    }

    private suspend fun quranSummaryDocument(profileId: String) =
        firestore.collection("users").document(ownerUid()).collection("quran_journeys").document(profileId)

    suspend fun backupQuranSummary(profileId: String, completedKhatmahs: Int, memorizedSurahs: Int) {
        quranSummaryDocument(profileId).set(mapOf(
            "completedKhatmahs" to completedKhatmahs,
            "memorizedSurahs" to memorizedSurahs
        ))
    }

    suspend fun deleteQuranSummary(profileId: String) {
        quranSummaryDocument(profileId).delete()
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

    private fun attempts(weekId: String) =
        firestore.collection("quiz_attempts").document(weekId).collection("entries")

    override suspend fun getOfficialQuizAttempt(weekId: String, userId: String): QuizScoreDto? {
        val snapshot = attempts(weekId).document(ownerUid())
            .get(dev.gitlive.firebase.firestore.Source.SERVER)
        return if (snapshot.exists) snapshot.data<QuizScoreDto>().copy(userId = userId) else null
    }

    override suspend fun reserveOfficialQuizAttempt(weekId: String, dto: QuizScoreDto): Pair<QuizScoreDto, Boolean> {
        val uid = ownerUid()
        val reservation = attempts(weekId).document(uid)
        val result = firestore.runTransaction {
            val saved = get(reservation)
            if (saved.exists) saved.data<QuizScoreDto>() to false
            else {
                set(reservation, dto.copy(userId = uid))
                dto to true
            }
        }
        return result.first.copy(userId = dto.userId) to result.second
    }

    override suspend fun finishOfficialQuizAttempt(weekId: String, userId: String, token: String, score: Int): QuizScoreDto {
        val reservation = attempts(weekId).document(ownerUid())
        return firestore.runTransaction {
            val saved = get(reservation).data<QuizScoreDto>()
            check(saved.token == token && token.isNotBlank()) { "Official attempt does not match" }
            if (saved.completed) saved
            else saved.copy(score = score, completed = true).also { set(reservation, it) }
        }.copy(userId = userId)
    }

    /** أعلى 3 أطفال في أسبوع معيّن — دول اللي بيظهروا في البوديوم */
    suspend fun getQuizWeekWinners(weekId: String): List<QuizScoreDto> {
        try {
            val snapshot = firestore.collection("quiz_podium")
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
