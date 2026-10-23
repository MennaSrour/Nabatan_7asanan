package com.menna.nabatan_7asena.presentation.screens.home

import com.menna.nabatan_7asena.core.util.PrayerPhase
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.TaskCategory

data class HomeUiState(
    val isLoading: Boolean = true,
    val user: UiUser? = null,
    val dailyWisdom: String = "جاري تحميل نور اليوم...",
    val hijriDate: String = "",
    val tasks: List<UiTaskItem> = emptyList(),
    val showCelebration: Boolean = false,
    val isNightTheme: Boolean = false,
    val hasBookmark: Boolean = false,
    val showChallengeDialog: Boolean = false,
    val currentRiddle: DailyContent.Riddle? = null,
    // فشل تحميل مهام/صلوات اليوم (الأغلب الأعم: مفيش إنترنت) — منفصل عمداً عن
    // isLoading: isLoading يعني "لسه بنجيب"، وده يعني "جبنا حاولنا وفشلنا".
    // من غيره كانت الشاشة بتفضل فاضية بصمت من غير أي تفسير للمستخدمة.
    val hasLoadError: Boolean = false,
    // إشارة أدق من hasLoadError: بعد إصلاح DailyActivityRepositoryImpl، فشل مواقيت
    // الصلاة بقى معزول ومابيسقّطش باقي اليوم (الأذكار/القرآن/التحدي بيفضلوا يظهروا).
    // العلامة دي بتتحسب من "مفيش ولا نشاط بتصنيف PRAYER في القايمة" — وده مينفعش
    // يحصل طبيعي غير في حالة فشل الجلب، لأن الصلوات الخمسة بتتضاف كلها مع بعض
    // أو ولا واحدة. بنستخدمها عشان نوري "مفيش نت" في مكان صف الصلوات بس، من غير
    // ما نخبي باقي المهام اللي فعلاً نجحت تتحمّل.
    val hasPrayerLoadError: Boolean = false
) {
    data class UiUser(
        val name: String,
        val avatarEmoji: String,
        val starsText: String,
        val streakText: String
    )

    data class UiTaskItem(
        val id: Int,
        val title: String,
        val subtitle: String? = null,
        val isCompleted: Boolean,
        val emoji: String,
        val category: TaskCategory,
        val isPlaying: Boolean = false,
        /** null لأي مهمة مش صلاة. */
        val prayerPhase: PrayerPhase? = null,
        /** الصلاة اللي عليها الدور: الحالية، أو الجاية لو لسه قبل الفجر. */
        val isPrayerFocused: Boolean = false
    )
}