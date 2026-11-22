package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.core.util.arabicIndic

internal const val BADGES_TITLE = "جوائزي"

internal enum class Badge(
    val title: String,
    val goal: String,
    val earnedMessage: String,
    val emblem: String,
    val tint: Color,
    val target: Int,
    val current: (BadgesUiState) -> Int
) {
    Dawn("وسام الفجر", "سجّل صلاة الفجر في ٣ أيام", "سجّلت صلاة الفجر في ٣ أيام", "🌅", Color(0xFFFFA166), 3, { it.fajrDays }),
    Quran("نور القرآن", "أكمل أول جزء من القرآن", "أكملت أول جزء من القرآن", "📖", Color(0xFF60CD9E), 1, { it.quranPartsFinished }),
    Remembrance("نجمة الأذكار", "سجّل ذكر الله في ٣ أيام", "سجّلت ذكر الله في ٣ أيام", "📿", Color(0xFF9D7DE0), 3, { it.azkarDays }),
    Garden("ساقي الحديقة", "اسقِ شجرتك لأول مرة", "سقيت شجرتك لأول مرة", "💧", Color(0xFF53C9EC), 1, { if (it.wateredGarden) 1 else 0 }),
    Friday("وسام الجمعة", "أكمل تحدّي الجمعة", "أكملت تحدّي الجمعة", "🏆", Color(0xFFFFB43C), 1, { if (it.finishedFriday) 1 else 0 }),
    Streak("أسبوع من الخير", "داوم على عمل طيب ٧ أيام متتالية", "داومت على عمل طيب ٧ أيام متتالية", "🌙", Color(0xFF8C83F5), 7, { it.currentStreak }),
    Stars("حديقة النجوم", "اجمع ٥٠ نجمة", "جمعت ٥٠ نجمة", "⭐", Color(0xFFF6A2C0), 50, { it.totalStars }),
    Completion("ختمة القرآن", "أكمل ٣٠ جزءًا من القرآن", "أكملت ٣٠ جزءًا من القرآن", "👑", Color(0xFFFFCB60), 30, { it.quranPartsFinished });

    fun isUnlocked(stats: BadgesUiState) = current(stats) >= target

    fun fraction(stats: BadgesUiState) = current(stats).coerceIn(0, target).toFloat() / target
}

internal fun BadgesUiState.unlockedCount() = Badge.entries.count { it.isUnlocked(this) }

internal fun BadgesUiState.badgeCountLabel() =
    "${arabicIndic(unlockedCount())} من ${arabicIndic(Badge.entries.size)} أوسمة"
