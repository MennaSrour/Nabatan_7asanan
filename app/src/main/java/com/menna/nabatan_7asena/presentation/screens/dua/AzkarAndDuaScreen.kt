package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.ui.theme.SummerTheme

// ─────────────────────────────────────────────────────────────
// الموديلات
// ─────────────────────────────────────────────────────────────

data class Prophet(
    val name: String,
    val emoji: String,
    val lesson: String,
    /** ⚠️ ضيفي هنا R.raw.<اسم الملف الصوتي> بتاع قصة النبي ده لما يكون جاهز */
    val audioRes: Int = 0
)

data class Zekr(
    val title: String,
    val text: String,
    val icon: String,
    /** ⚠️ ضيفي هنا R.raw.<اسم الملف الصوتي> بتاع الدعاء ده لما يكون جاهز */
    val audioRes: Int = 0
)

val prophetsList = listOf(
    Prophet("آدم عليه السلام", "🌍", "أول إنسان، وأول نبي، علّمه الله أسماء كل شيء"),
    Prophet("نوح عليه السلام", "🚢", "صبر على قومه سنين طويلة وبنى السفينة بأمر الله"),
    Prophet("إبراهيم عليه السلام", "🔥", "خليل الله، ونجّاه الله من النار فصارت بردًا وسلامًا"),
    Prophet("يوسف عليه السلام", "🌙", "أحسن القصص، صبر على المحن وعفا عن إخوته"),
    Prophet("أيوب عليه السلام", "🩹", "أشد الناس صبرًا على البلاء حتى فرّج الله كربه"),
    Prophet("موسى عليه السلام", "🌊", "كليم الله، فلق له البحر ونجّاه من فرعون"),
    Prophet("يونس عليه السلام", "🐋", "نجّاه الله من بطن الحوت بذكره وتسبيحه"),
    Prophet("سليمان عليه السلام", "👑", "أعطاه الله ملكًا عظيمًا وفهّمه منطق الطير والحيوان"),
    Prophet("محمد ﷺ", "🕌", "خاتم الأنبياء، وأرسله الله رحمة للعالمين")
)

val azkarList = listOf(
    Zekr(
        "دعاء دخول الخلاء",
        "بِسْمِ اللَّهِ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
        "🚪"
    ),
    Zekr(
        "دعاء الخروج من الخلاء",
        "غُفْرَانَكَ",
        "✅"
    ),
    Zekr(
        "دعاء دخول المنزل",
        "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
        "🏠"
    ),
    Zekr(
        "دعاء الخروج من المنزل",
        "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "🚶"
    ),
    Zekr(
        "دعاء لبس الثوب الجديد",
        "اللَّهُمَّ لَكَ الْحَمْدُ أَنْتَ كَسَوْتَنِيهِ، أَسْأَلُكَ مِنْ خَيْرِهِ وَخَيْرِ مَا صُنِعَ لَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّهِ وَشَرِّ مَا صُنِعَ لَهُ",
        "👕"
    ),
    Zekr(
        "كفارة المجلس",
        "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
        "🗣️"
    ),
    Zekr(
        "دعاء الحماية من الحسد والعين",
        "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
        "🛡️"
    ),
    Zekr(
        "دعاء النوم",
        "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        "🌙"
    )
)

// ─────────────────────────────────────────────────────────────
// الشاشة الرئيسية
// ─────────────────────────────────────────────────────────────

@Composable
fun AzkarAndDuaScreen(viewModel: DuaViewModel = hiltViewModel()) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: الأنبياء, 1: الحصن

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(SummerTheme.Colors.PrimaryTeal.copy(alpha = 0.08f), Color(0xFFFFFDE7))
                    )
                )
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("🌙", fontSize = 26.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "الأذكار والأدعية",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF37474F)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SectionButton(
                    text = "شجرة الأنبياء 🌳",
                    isSelected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    modifier = Modifier.weight(1f)
                )
                SectionButton(
                    text = "حصن المسلم 🛡️",
                    isSelected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Crossfade(targetState = selectedSection, label = "sections_fade") { section ->
                when (section) {
                    0 -> ProphetsPathList(onProphetClick = { viewModel.playAudio(it.audioRes) })
                    else -> AzkarList(onZekrClick = { viewModel.playAudio(it.audioRes) })
                }
            }
        }
    }
}

@Composable
fun SectionButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) SummerTheme.Colors.PrimaryTeal else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF37474F),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────
// قسم: شجرة الأنبياء
// ─────────────────────────────────────────────────────────────

@Composable
fun ProphetsPathList(onProphetClick: (Prophet) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
            drawLine(
                color = SummerTheme.Colors.PrimaryTeal.copy(alpha = 0.35f),
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 8.dp.toPx(),
                pathEffect = pathEffect
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp, /* extra bottom for nav bar */),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(prophetsList) { index, prophet ->
                val isLeft = index % 2 == 0
                ProphetPathNode(
                    prophet = prophet,
                    isLeft = isLeft,
                    onClick = { onProphetClick(prophet) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ProphetPathNode(prophet: Prophet, isLeft: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLeft) Spacer(modifier = Modifier.width(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(150.dp)
                .clickable { onClick() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
                    .border(3.dp, SummerTheme.Colors.PrimaryGold, CircleShape)
            ) {
                Text(text = prophet.emoji, fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFFFF9C4),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = prophet.name,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF5D4037),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = prophet.lesson,
                fontSize = 11.sp,
                color = Color(0xFF546E7A),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (isLeft) Spacer(modifier = Modifier.width(40.dp))
    }
}

// ─────────────────────────────────────────────────────────────
// قسم: حصن المسلم
// ─────────────────────────────────────────────────────────────

// لوحة ألوان بسيطة بتتلف على الكروت، مبنية على ألوان SummerTheme الأساسية
private val duaCardPalette = listOf(
    SummerTheme.Colors.PrimaryTeal,
    SummerTheme.Colors.PrimaryGold,
    SummerTheme.Colors.PrimaryPink
)

@Composable
fun AzkarList(onZekrClick: (Zekr) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(azkarList) { index, zekr ->
            val cardColor = duaCardPalette[index % duaCardPalette.size]
            DuaCard(zekr = zekr, accentColor = cardColor, onClick = { onZekrClick(zekr) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun DuaCard(zekr: Zekr, accentColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(zekr.icon, fontSize = 22.sp)
                }

                Spacer(Modifier.width(14.dp))

                Text(
                    text = zekr.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔊", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                color = Color(0xFFFFFDE7),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = zekr.text,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 15.sp,
                    color = Color(0xFF4E342E),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
