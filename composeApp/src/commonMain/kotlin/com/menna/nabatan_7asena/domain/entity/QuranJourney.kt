package com.menna.nabatan_7asena.domain.entity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

const val MUSHAF_PAGE_COUNT = 604
const val KHATMAH_BONUS_STARS = 100
const val MEMORIZATION_BONUS_STARS = 10
const val DAILY_QURAN_TASK_ID = 300

enum class WirdGoal(val pages: Int, val label: String) {
    ONE(1, "وجه واحد (صفحة يومياً)"),
    TWO(2, "وجهان (صفحتان يومياً)"),
    QUARTER(5, "ربع جزء (5 صفحات)"),
    HALF(10, "حزب / نصف جزء (10 صفحات)"),
    JUZ(20, "جزء كامل (20 صفحة)"),
    TWO_JUZ(40, "جزآن (40 صفحة)");

    val estimatedDays: Int get() = (MUSHAF_PAGE_COUNT + pages - 1) / pages
}

@Serializable
data class Wird(val firstPage: Int, val lastPage: Int)

@Serializable
data class Khatmah(val goal: WirdGoal, val pagesRead: Int = 0) {
    val nextWird: Wird get() = Wird(
        pagesRead + 1, (pagesRead + goal.pages).coerceAtMost(MUSHAF_PAGE_COUNT)
    )
}

@Serializable
enum class MemorizationStatus { NOT_STARTED, IN_PROGRESS, MEMORIZED }

@Serializable
data class SurahMemorization(
    val status: MemorizationStatus = MemorizationStatus.NOT_STARTED,
    val ayahs: Int = 0,
    val bonusAwarded: Boolean = false
)

@Serializable
data class QuranJourney(
    val activeKhatmah: Khatmah? = null,
    val lastWirdDate: LocalDate? = null,
    val lastWird: Wird? = null,
    val completedKhatmahs: Int = 0,
    val celebrationPending: Boolean = false,
    val memorization: Map<Int, SurahMemorization> = emptyMap()
) {
    val memorizedSurahs: Int get() = memorization.values.count { it.status == MemorizationStatus.MEMORIZED }

    fun wirdFor(date: LocalDate): Wird? =
        if (lastWirdDate == date) lastWird else activeKhatmah?.nextWird

    fun completeWird(date: LocalDate, expected: Wird): QuranJourney {
        val khatmah = activeKhatmah ?: return this
        if (lastWirdDate?.let { it >= date } == true || khatmah.nextWird != expected) return this
        val finished = expected.lastPage == MUSHAF_PAGE_COUNT
        return copy(
            activeKhatmah = if (finished) null else khatmah.copy(pagesRead = expected.lastPage),
            lastWirdDate = date,
            lastWird = expected,
            completedKhatmahs = completedKhatmahs + if (finished) 1 else 0,
            celebrationPending = celebrationPending || finished
        )
    }

    fun updateSurah(id: Int, status: MemorizationStatus, ayahs: Int, totalAyahs: Int): QuranJourney {
        require(id in 1..114 && totalAyahs > 0)
        val previous = memorization[id] ?: SurahMemorization()
        val next = SurahMemorization(
            status = status,
            ayahs = when (status) {
                MemorizationStatus.NOT_STARTED -> 0
                MemorizationStatus.IN_PROGRESS -> ayahs.coerceIn(0, totalAyahs)
                MemorizationStatus.MEMORIZED -> totalAyahs
            },
            bonusAwarded = previous.bonusAwarded || status == MemorizationStatus.MEMORIZED
        )
        return if (next == previous) this else copy(memorization = memorization + (id to next))
    }
}
