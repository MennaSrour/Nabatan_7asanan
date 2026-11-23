package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.domain.entity.KidsShort
import com.menna.nabatan_7asena.platform.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

internal class KidsShortsRepository(private val firebase: FirebaseDataSource) {
    suspend fun load(): List<KidsShort> {
        val clips = try {
            withTimeoutOrNull(4_000) { firebase.getKidsShorts() }.orEmpty()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w("KidsShorts", "Using bundled clips after a Firestore read failure")
            emptyList()
        }
        return clips.ifEmpty { Samples }
    }
}

private val Samples = listOf(
    sample("bismillah", "بسم الله", "آداب الطعام", 1),
    sample("salam", "السلام عليكم", "آداب التحية", 2),
    sample("alhamdulillah", "الحمد لله", "أذكار", 3)
)

private fun sample(id: String, title: String, category: String, order: Long): KidsShort {
    val path = "files/kids_shorts/$id"
    return KidsShort("sample-$id", title, "$path.mp4", "$path.jpg", category, order, bundled = true)
}
