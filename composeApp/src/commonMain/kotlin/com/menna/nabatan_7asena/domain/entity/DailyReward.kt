package com.menna.nabatan_7asena.domain.entity

internal val dailyGiftRequiredTaskIds = (100..104).toSet() + setOf(DAILY_QURAN_TASK_ID, 400)
internal const val DAILY_COMPLETION_REWARD_KIND = "daily_completion"

internal fun missingDailyGiftTasks(extraTaskIds: Iterable<Int>, completedTaskIds: Set<Int>): Int =
    ((dailyGiftRequiredTaskIds + extraTaskIds) - completedTaskIds).size

enum class KidsGame(val title: String, val hint: String) {
    MEMORY("لعبة الذاكرة", "اعثر على كل زوج من البطاقات"),
    VERSE("رتّب الآية", "اضغط على كلمات الآية بالترتيب"),
    TRIVIA("تحدي السرعة", "أجب عن الأسئلة في ٦٠ ثانية"),
    CATCH("التقاط الحسنات", "حرّك السلة والتقط النجوم"),
    RIDDLE("فزورة اليوم", "اختر الإجابة الصحيحة")
}

enum class WheelPrize(val label: String, val stars: Int = 0, val water: Int = 0, val game: KidsGame? = null) {
    MEMORY(KidsGame.MEMORY.title, game = KidsGame.MEMORY),
    WATER_3("٣ قطرات ماء", water = 3),
    VERSE(KidsGame.VERSE.title, game = KidsGame.VERSE),
    RIDDLE(KidsGame.RIDDLE.title, game = KidsGame.RIDDLE),
    TRIVIA(KidsGame.TRIVIA.title, game = KidsGame.TRIVIA),
    STARS_20("٢٠ نجمة", stars = 20),
    CATCH(KidsGame.CATCH.title, game = KidsGame.CATCH),
    STARS_50("٥٠ نجمة", stars = 50),
    STARS_10("١٠ نجوم", stars = 10),
    WATER_5("٥ قطرات ماء", water = 5);

    companion object {
        val segments = listOf(MEMORY, WATER_3, VERSE, RIDDLE, TRIVIA, STARS_20, CATCH)

        fun fromStoredCode(code: String): WheelPrize? =
            if (code == "BONUS_ROUND") MEMORY else entries.firstOrNull { it.name == code }
    }
}

data class DailyRewardStatus(
    val prize: WheelPrize? = null,
    val usedRounds: Set<KidsGame> = emptySet(),
    val earnedHere: Boolean = false,
    val waterDelivered: Boolean = false,
    val spinPending: Boolean = false,
    val clockRolledBack: Boolean = false
)

data class VerseBuilderItem(val reference: String, val words: List<String>)
data class SpeedQuestion(val prompt: String, val choices: List<String>, val answerIndex: Int)
