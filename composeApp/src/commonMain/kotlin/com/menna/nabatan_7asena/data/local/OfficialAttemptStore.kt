package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.domain.entity.OfficialQuizAttempt
import com.menna.nabatan_7asena.platform.PlatformPreferences
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface OfficialAttemptStorage { fun read(): String?; fun write(value: String) }
class PlatformOfficialAttemptStorage : OfficialAttemptStorage {
    private val prefs = PlatformPreferences("official_quiz_attempts")
    override fun read() = prefs.getString("attempts_v1")
    override fun write(value: String) = prefs.putString("attempts_v1", value)
}
class OfficialAttemptStore(private val storage: OfficialAttemptStorage = PlatformOfficialAttemptStorage()) {
    private val revision = MutableStateFlow(0L)

    fun observeCompleted(userId: String) = revision
        .map { all().values.any { it.userId == userId && it.completed } }
        .distinctUntilChanged()

    private val json = Json { ignoreUnknownKeys = true }
    private fun all(): Map<String, OfficialQuizAttempt> {
        val saved = storage.read()?.let { json.decodeFromString<Map<String, OfficialQuizAttempt>>(it) } ?: emptyMap()
        saved.forEach { (key, attempt) ->
            require(key == "${attempt.userId}|${attempt.weekId}" && attempt.score in 0..5)
            require(attempt.userId.isNotBlank() && (attempt.completed || attempt.token.isNotBlank()))
            kotlinx.datetime.LocalDate.parse(attempt.weekId)
        }
        return saved
    }
    fun get(userId: String, weekId: String) = all()["$userId|$weekId"]
    fun pending(userId: String) = all().values.filter { it.userId == userId && it.completed && !it.synced }
    fun put(attempt: OfficialQuizAttempt) {
        require(attempt.userId.isNotBlank() && attempt.score in 0..5)
        kotlinx.datetime.LocalDate.parse(attempt.weekId)
        storage.write(json.encodeToString(all() + ("${attempt.userId}|${attempt.weekId}" to attempt)))
        revision.update { it + 1 }
    }
}
