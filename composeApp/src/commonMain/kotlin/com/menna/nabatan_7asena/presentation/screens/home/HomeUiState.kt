package com.menna.nabatan_7asena.presentation.screens.home

import com.menna.nabatan_7asena.core.util.PrayerPhase
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.TaskCategory

data class HomeUiState(
    val isLoading: Boolean = true,
    val suggestions: com.menna.nabatan_7asena.domain.entity.ExtraTasks = com.menna.nabatan_7asena.domain.entity.ExtraTasks(emptyList(), emptyList()),
    val user: UiUser? = null,
    val dailyWisdom: String = "جاري تحميل نور اليوم...",
    val hijriDate: String = "",
    val tasks: List<UiTaskItem> = emptyList(),
    val gardenEarnedStars: Int = 0,
    val isGardenShopBusy: Boolean = false,
    val gardenShopError: com.menna.nabatan_7asena.presentation.common.GenderedString? = null,
    val gardenPurchaseSequence: Long = 0,
    val gardenProfileId: String = "",
    val garden: com.menna.nabatan_7asena.domain.entity.GardenProgress? = null,
    val isGardenUnavailable: Boolean = false,
    val isWateringGarden: Boolean = false,
    val gardenSaveFailed: Boolean = false,
    val gardenWateringSequence: Long = 0,
    val gardenNeedsWater: Boolean = false,
    val prayerDate: kotlinx.datetime.LocalDate? = null,
    val previousIshaMinutes: Int? = null,
    val tomorrowFajrMinutes: Int? = null,
    val showCelebration: Boolean = false,
    val isNightTheme: Boolean = false,
    val hasBookmark: Boolean = false,
    val dailyGiftRemaining: Int = com.menna.nabatan_7asena.domain.entity.dailyGiftRequiredTaskIds.size,
    val dailyGiftTotal: Int = com.menna.nabatan_7asena.domain.entity.dailyGiftRequiredTaskIds.size,
    val dailyRiddle: DailyContent.Riddle? = null,
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
    val isDailyGiftUnlocked: Boolean get() = !isLoading && !hasLoadError && dailyGiftRemaining == 0

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
