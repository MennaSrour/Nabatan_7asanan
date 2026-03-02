package com.menna.nabata_7asena.presentation.screens.home.component

import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import com.menna.nabata_7asena.ui.theme.RamadanTheme

private val WarmNight = Color(0xFF1E1240)
private val MidNight = Color(0xFF2D1B69)
private val StarYellow = Color(0xFFFFF176)
private val GoldWarm = Color(0xFFFFB74D)
private val GoldDeep = Color(0xFFE65100)
private val MoonCream = Color(0xFFFFF9C4)

@Composable
fun ColorfulHeaderRamadan(
    user: HomeUiState.UiUser?,
    hijriDate: String,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var isTalking by remember { mutableStateOf(false) }

        val mediaPlayer = remember {
            try {
                MediaPlayer.create(context, R.raw.welcome_kids)?.apply {
                    setOnCompletionListener { isTalking = false; seekTo(0) }
                }
            } catch (e: Exception) {
                null
            }
        }
        DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

        val floatingOffset = RamadanTheme.rememberFloatingAnimation()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(WarmNight, MidNight, Color(0xFF3949AB).copy(0.7f))
                    )
                )
                val stars = listOf(
                    Pair(0.12f, 0.10f), Pair(0.35f, 0.08f), Pair(0.65f, 0.12f),
                    Pair(0.82f, 0.07f), Pair(0.50f, 0.05f), Pair(0.25f, 0.18f),
                    Pair(0.90f, 0.15f), Pair(0.08f, 0.22f), Pair(0.72f, 0.20f),
                )
                stars.forEach { (x, y) ->
                    drawCircle(
                        color = StarYellow.copy(0.7f),
                        radius = 2.5.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            size.width * x,
                            size.height * y
                        )
                    )
                }
            }

            val crescentComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.crescent_glow))
            LottieAnimation(
                composition = crescentComp,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 38.dp, end = 16.dp)
            )

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, start = 12.dp)
            ) {
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = Color.White.copy(0.85f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.88f)
                    quadraticBezierTo(
                        size.width * 0.5f,
                        size.height * 1.0f,
                        0f,
                        size.height * 0.88f
                    )
                    close()
                }
                drawPath(
                    path = path, brush = Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF1A0A4E).copy(0.5f))
                    )
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 46.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .offset(y = floatingOffset.dp)
                            .clickable {
                                if (mediaPlayer != null) {
                                    if (mediaPlayer.isPlaying) {
                                        mediaPlayer.pause(); mediaPlayer.seekTo(0); isTalking =
                                            false
                                    } else {
                                        mediaPlayer.start(); isTalking = true
                                    }
                                }
                            }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(
                                3.dp,
                                Brush.linearGradient(listOf(GoldWarm, GoldDeep))
                            ),
                            shadowElevation = 10.dp,
                            modifier = Modifier.size(86.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.welcome),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        if (!isTalking) {
                            Surface(
                                color = GoldWarm,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-4).dp)
                            ) {
                                Text(
                                    "رمضان كريم! 🌙",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "${user?.name ?: "بطلنا"} 🌟",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            color = GoldWarm.copy(0.25f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "🕌 $hijriDate",
                                fontSize = 12.sp,
                                color = MoonCream,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RamadanStatCard(
                        emoji = "⭐",
                        value = user?.starsText?.replace("⭐", "")?.trim() ?: "0",
                        label = "نجمة",
                        cardColor = Color(0xFFFF8F00),
                        modifier = Modifier.weight(1f)
                    )
                    RamadanStatCard(
                        emoji = "🌙",
                        value = user?.streakText?.replace("أيام", "")?.trim() ?: "0",
                        label = "يوم",
                        cardColor = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RamadanStatCard(
    emoji: String,
    value: String,
    label: String,
    cardColor: Color,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(0.12f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(cardColor.copy(0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
                Text(
                    label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Color.White.copy(0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksListRamadan(
    tasks: List<HomeUiState.UiTaskItem>,
    onTaskClick: (HomeUiState.UiTaskItem) -> Unit,
    onPlaySound: (HomeUiState.UiTaskItem) -> Unit,
    wisdom: String,
    onTreasureClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { DailyWisdomCardRamadan(wisdom = wisdom) }
        item {
            val comp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lights))
            LottieAnimation(
                composition = comp,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        items(items = tasks, key = { it.id }) { item ->
            Box(modifier = Modifier.animateItem()) {
                TaskBubbleCardRamadan(
                    item = item,
                    onClick = { onTaskClick(item) },
                    onPlaySound = { onPlaySound(item) }
                )
            }
        }

        item {
            TreasureChestCardRamadan(
                isUnlocked = tasks.isNotEmpty() && tasks.all { it.isCompleted },
                onClick = onTreasureClick
            )
        }
    }
}

@Composable
fun TaskBubbleCardRamadan(
    item: HomeUiState.UiTaskItem,
    onClick: () -> Unit,
    onPlaySound: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (item.isCompleted) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val bgColor by animateColorAsState(
        targetValue = item.backgroundColor,
        animationSpec = tween(500),
        label = "bgColor"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize()
            .shadow(
                elevation = if (item.isCompleted) 2.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = item.backgroundColor.copy(0.4f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(bgColor, bgColor.copy(0.75f))
                    )
                )
                .run {
                    if (item.isCompleted)
                        border(
                            2.dp,
                            RamadanTheme.Colors.TaskCompletedGreen,
                            RoundedCornerShape(20.dp)
                        )
                    else
                        border(1.5.dp, Color.White.copy(0.2f), RoundedCornerShape(20.dp))
                }
                .padding(vertical = 18.dp, horizontal = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.White.copy(0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(item.emoji, fontSize = 30.sp) }

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.contentColor,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                    )
                    if (item.subtitle != null) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            item.subtitle,
                            fontSize = 14.sp,
                            color = item.contentColor.copy(0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!item.isCompleted && item.category != TaskCategory.EXTRA) {
                    Surface(
                        onClick = onPlaySound,
                        shape = CircleShape,
                        color = Color.White.copy(0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (item.isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                contentDescription = null,
                                tint = if (item.isPlaying) Color.White else Color.White.copy(0.45f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (item.isCompleted) RamadanTheme.Colors.TaskCompletedGreen else Color.White.copy(
                                0.25f
                            ),
                            CircleShape
                        )
                        .border(2.dp, Color.White.copy(0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) Icon(
                        Icons.Filled.Check,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    else Text("🌙", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun DailyWisdomCardRamadan(wisdom: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFD700).copy(0.3f))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFFFF8DC), Color(0xFFFFF3B0))))
            .border(2.dp, Color(0xFFFFD700).copy(0.5f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFFFF8F00), Color(0xFFFF6F00))),
                        CircleShape
                    )
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) { Text("🌟", fontSize = 26.sp) }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "نور اليوم ✨",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE65100)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    wisdom,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2800),
                    lineHeight = 26.sp
                )
            }
        }
    }
}

@Composable
fun TreasureChestCardRamadan(isUnlocked: Boolean, onClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.treasure))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isUnlocked,
        iterations = LottieConstants.IterateForever
    )
    val pulseScale = if (isUnlocked) RamadanTheme.rememberPulseAnimation() else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .scale(pulseScale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isUnlocked)
                    Brush.linearGradient(listOf(Color(0xFFFF8F00), Color(0xFFE65100)))
                else
                    Brush.linearGradient(listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC)))
            )
            .border(
                3.dp,
                if (isUnlocked) Color(0xFFFFD700) else Color.Gray,
                RoundedCornerShape(20.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isUnlocked) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Text("🔒", fontSize = 38.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    if (isUnlocked) "🎁 هديتك جاهزة!" else "🔐 الكنز مقفول",
                    color = if (isUnlocked) Color.White else Color.Gray,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    if (isUnlocked) "اضغط عشان تفتح الهدية 🌙" else "خلص مهامك عشان تكسب",
                    color = if (isUnlocked) Color.White.copy(0.88f) else Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CelebrationOverlayRamadan(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var mp by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(Unit) {
        try {
            mp = MediaPlayer.create(context, R.raw.big_star_voice)?.apply {
                setOnCompletionListener {
                    try {
                        release()
                    } catch (e: Exception) {
                    }; mp = null
                }
                start()
            }
        } catch (e: Exception) {
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            try {
                mp?.let { if (it.isPlaying) it.stop(); it.release() }
            } catch (e: Exception) {
            }; mp = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmNight.copy(0.88f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoubleLottieCelebration(
                    spec1 = LottieCompositionSpec.RawRes(R.raw.win_celebration),
                    spec2 = LottieCompositionSpec.RawRes(R.raw.big_star)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "🌙 ماشاء الله عليك! ⭐",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "أخذت أكبر نجمة رمضانية",
                    color = GoldWarm,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DoubleLottieCelebration(spec1: LottieCompositionSpec, spec2: LottieCompositionSpec) {
    val c1 by rememberLottieComposition(spec1)
    val c2 by rememberLottieComposition(spec2)
    Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
        LottieAnimation(
            c1,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize()
        )
        LottieAnimation(
            c2,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(110.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheetRamadan(
    onDismiss: () -> Unit,
    onTaskSelected: (String) -> Unit,
    suggestions: ExtraTasks
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("نوافل") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFFF8E1),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 48.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌙", fontSize = 26.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "إضافة عمل صالح",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WarmNight
                )
                Spacer(Modifier.width(8.dp))
                Text("🌿", fontSize = 26.sp)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("نوافل", "قرآن", "مهمات", "أذكار").forEach { cat ->
                    val sel = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (sel) GoldWarm else Color(0xFFEFE8D5))
                            .border(
                                2.dp,
                                if (sel) GoldDeep else Color.Transparent,
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            cat,
                            color = if (sel) Color.White else Color(0xFF6D4C41),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val items = when (selectedCategory) {
                    "نوافل" -> listOf(
                        "سنة الفجر (ركعتين)",
                        "صلاة الضحى",
                        "سنة الظهر",
                        "سنة المغرب",
                        "سنة العشاء",
                        "قيام الليل",
                        "صلاة الوتر"
                    )

                    "قرآن" -> suggestions.werd
                    "مهمات" -> suggestions.challenges
                    else -> listOf(
                        "أذكار الصباح",
                        "أذكار المساء",
                        "أذكار النوم",
                        "استغفار (100)",
                        "صلاة على النبي (100)"
                    )
                }
                items(items) { title ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.5.dp, GoldWarm.copy(0.3f), RoundedCornerShape(14.dp))
                            .clickable { onTaskSelected(title); onDismiss() }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3E2723)
                        )
                        Text("🌙", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyChallengeDialogRamadan(
    riddle: DailyContent.Riddle,
    onDismiss: () -> Unit,
    onAnswerSelected: (Boolean) -> Unit
) {
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var isSolved by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun check(index: Int) {
        selectedIdx = index
        if (riddle.options[index] == riddle.answer) {
            isSolved = true
            val mp = MediaPlayer.create(context, R.raw.good)
            mp.setOnCompletionListener { it.release() }
            mp.start()
            onAnswerSelected(true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFF8E1))
                .border(3.dp, GoldWarm, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌙", fontSize = 26.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "فزورة رمضان",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = WarmNight
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("⭐", fontSize = 26.sp)
                }
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(1.5.dp, GoldWarm.copy(0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        riddle.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = WarmNight,
                        lineHeight = 26.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(18.dp))
                riddle.options.forEachIndexed { index, option ->
                    val isCorrect = option == riddle.answer
                    val bgColor by animateColorAsState(
                        when {
                            isSolved && isCorrect -> RamadanTheme.Colors.TaskCompletedGreen
                            selectedIdx == index && !isCorrect -> Color(0xFFEF5350)
                            else -> Color.White
                        }, label = "color"
                    )
                    Button(
                        onClick = { if (!isSolved) check(index) },
                        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (bgColor == Color.White) GoldWarm.copy(0.3f) else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            option,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bgColor == Color.White) WarmNight else Color.White
                        )
                    }
                }
                if (isSolved) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", fontSize = 22.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ماشاء الله! أحسنت ⭐",
                            color = RamadanTheme.Colors.TaskCompletedGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            if (isSolved) {
                val comp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
                LottieAnimation(
                    comp,
                    iterations = 1,
                    modifier = Modifier
                        .matchParentSize()
                        .scale(1.2f)
                )
            }
        }
    }
}