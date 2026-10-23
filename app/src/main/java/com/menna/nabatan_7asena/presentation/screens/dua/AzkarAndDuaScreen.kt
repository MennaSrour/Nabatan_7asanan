package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.random.Random



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



private object DuaSpacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 14.dp
    val l = 20.dp
    val xl = 32.dp
}

private object DuaRadius {
    val chip = 14.dp
    val card = 18.dp
    val pill = 22.dp
}

private val arabicIndicNumerals = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
private fun Int.toArabicIndic(): String =
    toString().map { ch -> arabicIndicNumerals.getOrElse(ch - '0') { ch } }.joinToString("")



@Composable
fun AzkarAndDuaScreen(viewModel: DuaViewModel = hiltViewModel()) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: الأنبياء, 1: الحصن
    val playingResId by viewModel.playingResId.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(SummerTheme.colors.wisdomStart, SummerTheme.colors.dayBackground)
                    )
                )
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader()

            Spacer(modifier = Modifier.height(DuaSpacing.l))

            SectionSwitcher(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it }
            )

            Spacer(modifier = Modifier.height(DuaSpacing.l))

            Crossfade(targetState = selectedSection, label = "sections_fade") { section ->
                when (section) {
                    0 -> ProphetsPathList(
                        playingResId = playingResId,
                        onProphetClick = { viewModel.toggleAudio(it.audioRes) }
                    )
                    else -> AzkarList(
                        playingResId = playingResId,
                        onZekrClick = { viewModel.toggleAudio(it.audioRes) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = DuaSpacing.l)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.NightsStay,
                    contentDescription = null,
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(DuaSpacing.m))

            Column {
                Text(
                    genderedStringResource(GenderedStrings.duaTitle),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SummerTheme.colors.textDarkBrown
                )
                Text(
                    genderedStringResource(GenderedStrings.duaDescription),
                    fontSize = 12.sp,
                    color = SummerTheme.colors.textBrown.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(DuaSpacing.m))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.9f),
            thickness = 1.dp,
            color = SummerTheme.colors.textBrown.copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun SectionSwitcher(selectedSection: Int, onSectionSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(DuaRadius.pill),
        color = SummerTheme.colors.cardBackground,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SectionButton(
                text = genderedStringResource(GenderedStrings.duaProphets),
                icon = R.drawable.prayer,
                isSelected = selectedSection == 0,
                onClick = { onSectionSelected(0) },
                modifier = Modifier.weight(1f)
            )
            SectionButton(
                text = genderedStringResource(GenderedStrings.duaFortress),
                icon = R.drawable.hisn,
                isSelected = selectedSection == 1,
                onClick = { onSectionSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SectionButton(
    text: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SummerTheme.colors.goldWarm else Color.Transparent,
        animationSpec = tween(220),
        label = "section_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) SummerTheme.colors.white else SummerTheme.colors.textDarkBrown,
        animationSpec = tween(220),
        label = "section_content"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClickLabel = text) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun ProphetsPathList(playingResId: Int?, onProphetClick: (Prophet) -> Unit) {
    val stoneColor = SummerTheme.colors.textBrown

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepY = 46.dp.toPx()
            var y = 0f
            val rnd = Random(42)
            while (y < size.height) {
                val radius = (2 + rnd.nextInt(2)).dp.toPx()
                val jitterX = (rnd.nextInt(7) - 3).dp.toPx()
                drawCircle(
                    color = stoneColor.copy(alpha = 0.18f),
                    radius = radius,
                    center = Offset(size.width / 2 + jitterX, y)
                )
                y += stepY
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(prophetsList) { index, prophet ->
                ProphetPathNode(
                    prophet = prophet,
                    order = index + 1,
                    isLeft = index % 2 == 0,
                    isPlaying = playingResId != 0 && playingResId == prophet.audioRes,
                    onClick = { onProphetClick(prophet) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ProphetMedallionIcon(icon: Int, sizeDp: Int) {
    Image(
        painter = painterResource(id = icon),
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProphetPathNode(
    prophet: Prophet,
    order: Int,
    isLeft: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLeft) Spacer(modifier = Modifier.width(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (!isLeft) ProphetPlayButton(isPlaying = isPlaying, onClick = onClick)

            Box(modifier = Modifier.size(84.dp), contentAlignment = Alignment.TopEnd) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, clip = false)
                        .border(
                            2.dp,
                            if (isPlaying) SummerTheme.colors.statGreenStart else SummerTheme.colors.goldWarm,
                            CircleShape
                        )
                ) {
                    ProphetMedallionIcon(prophet.icon, sizeDp = 76)
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(SummerTheme.colors.cardBackground, CircleShape)
                        .border(1.5.dp, SummerTheme.colors.goldWarm, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.toString(),
                        color = SummerTheme.colors.textDarkBrown,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SummerTheme.colors.cardBackground)
                    .border(
                        1.5.dp,
                        if (isPlaying) SummerTheme.colors.statGreenStart.copy(alpha = 0.6f)
                        else SummerTheme.colors.textBrown.copy(alpha = 0.25f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(onClickLabel = prophet.name) { onClick() }
                    .semantics { role = Role.Button }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = prophet.name,
                        fontWeight = FontWeight.Black,
                        color = SummerTheme.colors.textDarkBrown,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = prophet.lesson,
                        fontSize = 11.sp,
                        color = SummerTheme.colors.textBrown.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }

            if (isLeft) ProphetPlayButton(isPlaying = isPlaying, onClick = onClick)
        }

        if (isLeft) Spacer(modifier = Modifier.width(20.dp))
    }
}


@Composable
private fun ProphetPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isPlaying) SummerTheme.colors.statGreenStart else SummerTheme.colors.statGreenStart.copy(alpha = 0.55f),
        animationSpec = tween(200),
        label = "prophet_play_bg"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(28.dp)
            .background(backgroundColor, CircleShape)
            .clickable(onClickLabel = if (isPlaying) genderedStringResource(GenderedStrings.duaStopStory) else genderedStringResource(GenderedStrings.duaListenStory)) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
            contentDescription = null,
            tint = SummerTheme.colors.white,
            modifier = Modifier.size(15.dp)
        )
    }
}



@Composable
fun AzkarList(playingResId: Int?, onZekrClick: (Zekr) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(azkarList) { index, zekr ->
            DuaCard(
                zekr = zekr,
                order = index + 1,
                isPlaying = playingResId != 0 && playingResId == zekr.audioRes,
                onClick = { onZekrClick(zekr) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun DuaCard(zekr: Zekr, order: Int, isPlaying: Boolean, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DuaRadius.card),
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        border = if (isDark) BorderStroke(
            1.dp,
            SummerTheme.colors.white.copy(alpha = 0.08f)
        ) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SummerTheme.colors.goldWarm.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(zekr.icon),
                        contentDescription = null,
                        tint = SummerTheme.colors.goldWarm,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(DuaSpacing.s))

                Text(
                    text = order.toArabicIndic(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.textBrown.copy(alpha = 0.5f)
                )

                Spacer(Modifier.width(DuaSpacing.s))

                Text(
                    text = zekr.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.textDarkBrown,
                    modifier = Modifier.weight(1f)
                )

                val buttonBg by animateColorAsState(
                    targetValue = if (isPlaying) SummerTheme.colors.goldWarm
                    else SummerTheme.colors.goldWarm.copy(alpha = 0.14f),
                    animationSpec = tween(200),
                    label = "dua_play_bg"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isPlaying) SummerTheme.colors.white else SummerTheme.colors.goldWarm,
                    animationSpec = tween(200),
                    label = "dua_play_icon"
                )

                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(buttonBg, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                        contentDescription = if (isPlaying) genderedStringResource(GenderedStrings.duaStopAudio, zekr.title) else genderedStringResource(GenderedStrings.duaListenAudio, zekr.title),
                        tint = iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(Modifier.height(DuaSpacing.m))

            HorizontalDivider(
                thickness = 1.dp,
                color = SummerTheme.colors.textBrown.copy(alpha = 0.1f)
            )

            Spacer(Modifier.height(DuaSpacing.m))

            Text(
                text = zekr.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SummerTheme.colors.textDarkBrown,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
    }
}