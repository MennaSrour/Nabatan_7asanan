package com.menna.nabatan_7asena.domain.entity

enum class WheelPrize(val label: String, val stars: Int = 0, val water: Int = 0) {
    STARS_20("٢٠ نجمة", stars = 20),
    WATER_3("٣ قطرات", water = 3),
    STARS_50("٥٠ نجمة", stars = 50),
    BONUS_ROUND("جولة خاصة"),
    STARS_10("١٠ نجوم", stars = 10),
    WATER_5("٥ قطرات", water = 5)
}

enum class KidsGame(val title: String, val icon: String, val hint: String) {
    MEMORY("لعبة الذاكرة", "☾", "اعثر على كل زوج من البطاقات"),
    VERSE("رتّب الآية", "❋", "اضغط على كلمات الآية بالترتيب"),
    TRIVIA("تحدي السرعة", "⚡", "أجب عن الأسئلة في ٦٠ ثانية"),
    CATCH("التقاط الحسنات", "★", "حرّك السلة والتقط النجوم"),
    BONUS("جولة الذاكرة الخاصة", "🎁", "اعثر على ثمانية أزواج")
}

data class DailyRewardStatus(
    val prize: WheelPrize? = null,
    val rewardedGames: Set<KidsGame> = emptySet(),
    val earnedHere: Boolean = false,
    val waterDelivered: Boolean = false,
    val spinPending: Boolean = false
)

data class VerseBuilderItem(val reference: String, val words: List<String>)
data class SpeedQuestion(val prompt: String, val choices: List<String>, val answerIndex: Int)
