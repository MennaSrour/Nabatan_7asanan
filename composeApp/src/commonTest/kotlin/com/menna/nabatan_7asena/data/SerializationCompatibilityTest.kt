package com.menna.nabatan_7asena.data

import com.menna.nabatan_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto
import com.menna.nabatan_7asena.domain.entity.PrayerTimes
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SerializationCompatibilityTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun existingGsonPrayerCacheRemainsReadable() {
        val cached = """{"fajr":"04:12","dhuhr":"12:05","asr":"15:34","maghrib":"18:01","isha":"19:20"}"""
        val expected = PrayerTimes("04:12", "12:05", "15:34", "18:01", "19:20")

        assertEquals(expected, json.decodeFromString<PrayerTimes>(cached))
        assertEquals(expected, json.decodeFromString<PrayerTimes>(json.encodeToString(expected)))
    }

    @Test
    fun aladhanResponseUsesExistingCapitalizedTimingKeysAndIgnoresExtraFields() {
        val response = """{
            "code":200,"status":"OK","data":{
                "timings":{"Fajr":"04:12","Dhuhr":"12:05","Asr":"15:34","Maghrib":"18:01","Isha":"19:20","Sunrise":"05:40"},
                "date":{"hijri":{"date":"01-09-1447","month":{"number":9}}},
                "meta":{"timezone":"Africa/Cairo"}
            }
        }"""

        val decoded = json.decodeFromString<PrayerApiResponse>(response)

        assertEquals(PrayerTimes("04:12", "12:05", "15:34", "18:01", "19:20"), decoded.toEntity())
        assertEquals("01-09-1447", decoded.data.date.hijri.date)
    }

    @Test
    fun olderDailyDocumentsDefaultMissingFieldsAndIgnoreRetiredMission() {
        val document = """{"wisdom":"حكمة اليوم","challenge":"retired","riddleQuestion":"سؤال؟"}"""

        val decoded = json.decodeFromString<DailyPlanDto>(document)

        assertEquals("حكمة اليوم", decoded.wisdom)
        assertEquals("سؤال؟", decoded.riddleQuestion)
        assertEquals("", decoded.quranDhuhr)
        assertFalse(json.encodeToString(decoded).contains("challenge"))
    }

    @Test
    fun olderLeaderboardDocumentsKeepDefaultAvatarAndGender() {
        val decoded = json.decodeFromString<UserRankDto>("""{"userId":"child-1","name":"نور","totalStars":42}""")

        assertEquals("child-1", decoded.userId)
        assertEquals(42, decoded.totalStars)
        assertEquals("BOY", decoded.gender)
        assertEquals(0, decoded.avatarIndex)
    }
}
