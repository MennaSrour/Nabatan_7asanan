package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R

data class Prophet(
    val name: String,
    val icon: Int,
    val medallionColors: List<Color>,
    val lesson: String,
    val audioRes: Int = 0
)

data class Zekr(
    val title: String,
    val text: String,
    val icon: Int,
    val audioRes: Int = 0
)

val prophetsList = listOf(
    Prophet(
        "آدم عليه السلام",
        R.drawable.adam,
        listOf(Color(0xFF64B5F6), Color(0xFF66BB6A)),
        "أول إنسان، وأول نبي، علّمه الله أسماء كل شيء",
        R.raw.adam
    ),
    Prophet(
        "نوح عليه السلام",
        R.drawable.no7,
        listOf(Color(0xFF4FC3F7), Color(0xFF1976D2)),
        "صبر على قومه سنين طويلة وبنى السفينة بأمر الله",
        R.raw.no7
    ),
    Prophet(
        "إبراهيم عليه السلام",
        R.drawable.irahim,
        listOf(Color(0xFFFFA726), Color(0xFFE64A19)),
        "خليل الله، ونجّاه الله من النار فصارت بردًا وسلامًا",
        R.raw.ibrahim
    ),
    Prophet(
        "يوسف عليه السلام",
        R.drawable.yousef,
        listOf(Color(0xFF7986CB), Color(0xFF3F51B5)),
        "أحسن القصص، صبر على المحن وعفا عن إخوته",
        R.raw.yousef
    ),
    Prophet(
        "أيوب عليه السلام",
        R.drawable.auob,
        listOf(Color(0xFFFFAB91), Color(0xFFE57373)),
        "أشد الناس صبرًا على البلاء حتى فرّج الله كربه",
        R.raw.auop
    ),
    Prophet(
        "موسى عليه السلام",
        R.drawable.mousa,
        listOf(Color(0xFF90A4AE), Color(0xFF37474F)),
        "كليم الله، فلق له البحر ونجّاه من فرعون",
        R.raw.mousa
    ),
    Prophet(
        "يونس عليه السلام",
        R.drawable.younes,
        listOf(Color(0xFF4FC3F7), Color(0xFF0D47A1)),
        "نجّاه الله من بطن الحوت بذكره وتسبيحه",
        R.raw.younes
    ),
    Prophet(
        "سليمان عليه السلام",
        R.drawable.soliman,
        listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
        "أعطاه الله ملكًا عظيمًا وفهّمه منطق الطير والحيوان",
        R.raw.soliman
    ),
    Prophet(
        "محمد ﷺ",
        R.drawable.mohamed,
        listOf(Color(0xFFFFC83D), Color(0xFFB8860B)),
        "خاتم الأنبياء، وأرسله الله رحمة للعالمين",
        R.raw.mohammed
    )
)

val azkarList = listOf(
    Zekr(
        "دعاء دخول الخلاء",
        "بِسْمِ اللَّهِ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
        R.drawable.person,
        R.raw.in_bath
    ),
    Zekr(
        "دعاء الخروج من الخلاء",
        "غُفْرَانَكَ",
        R.drawable.check,
        R.raw.out_bath
    ),
    Zekr(
        "دعاء دخول المنزل",
        "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
        R.drawable.login,
        R.raw.in_home
    ),
    Zekr(
        "دعاء الخروج من المنزل",
        "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        R.drawable.logout,
        R.raw.out_home
    ),
    Zekr(
        "دعاء لبس الثوب الجديد",
        "اللَّهُمَّ لَكَ الْحَمْدُ أَنْتَ كَسَوْتَنِيهِ، أَسْأَلُكَ مِنْ خَيْرِهِ وَخَيْرِ مَا صُنِعَ لَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّهِ وَشَرِّ مَا صُنِعَ لَهُ",
        R.drawable.shirt,
        R.raw.lips_thawp
    ),
    Zekr(
        "كفارة المجلس",
        "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
        R.drawable.meeting,
        R.raw.kafarat_maglis
    ),
    Zekr(
        "دعاء الحماية من الحسد والعين",
        "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
        R.drawable.hisn,
        R.raw.hasad
    ),
    Zekr(
        "دعاء النوم",
        "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        R.drawable.sleep,
        R.raw.sleeping
    ),
    Zekr(
        "دعاء قبل الطعام",
        "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ النَّارِ، بِسْمِ اللَّهَِ",
        R.drawable.start_foods,
        R.raw.start_food
    ),
    Zekr(
        "دعاء الإنتهاء من الطعام",
        "الحمدُ للَّهِ الَّذي أطعَمَنا وسقانا وجعلنا مُسلمينَ",
        R.drawable.end_foods,
        R.raw.end_food
    )
)

internal object DuaSpacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 14.dp
    val l = 20.dp
    val xl = 32.dp
}

internal object DuaRadius {
    val chip = 14.dp
    val card = 18.dp
    val pill = 22.dp
}

internal val arabicIndicNumerals = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
