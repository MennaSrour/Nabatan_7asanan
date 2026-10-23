package com.menna.nabatan_7asena.presentation.screens.home.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun TreasureChestCard(
    isUnlocked: Boolean,
    completedCount: Int = 0,
    totalCount: Int = 0,
    onClick: () -> Unit
) {
    val progress = if (totalCount > 0) {
        (completedCount.toFloat() / totalCount).coerceIn(0f, 1f)
    } else {
        0f
    }

    // نفس الملف بيرسم الصندوق مقفول (فريم صفر) ولحظة الفتح لحد آخره — مش محتاجين
    // ملفين، محتاجين نتحكم إحنا في مكان الأنيميشن بدل ما نسيبها تلف لوحدها.
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.treasure))
    val chestAnimatable = rememberLottieAnimatable()

    // بيتحرك مرة واحدة بس، بالظبط لحظة ما isUnlocked تتحول من false لـ true —
    // مش كل ما الكارت يترسم من جديد. وبعد ما تخلص، بتفضل واقفة على آخر فريم
    // (الصندوق مفتوح) من غير أي كود إضافي، لأن LottieAnimatable بيحتفظ بآخر قيمة
    // وصلها. لو رجع يوم جديد وقفل تاني، بترجع تقف على فريم صفر (مقفول) تلقائيًا.
    LaunchedEffect(composition, isUnlocked) {
        val loadedComposition = composition ?: return@LaunchedEffect
        if (isUnlocked) {
            chestAnimatable.animate(composition = loadedComposition, iterations = 1)
        } else {
            chestAnimatable.snapTo(loadedComposition, progress = 0f)
        }
    }

    val infinite = rememberInfiniteTransition(label = "chest")
    // نبضة خفيفة مستمرة على الصندوق سواء مقفول أو مفتوح — عشان الكارت
    // مايبقاش "واقف" تمامًا بين لحظة وأخرى
    val breathe by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "breathe"
    )
    // توهج بيدور ببطء خلف الصندوق وقت الفتح — إحساس "لمعة" هادي من غير مبالغة
    val glowAngle = SummerTheme.rememberSlowRotation(durationMillis = 9000)
    // نقط بريق صغيرة حوالين الكارت — استخدمنا الدالة الجاهزة في الثيم بدل
    // ما نخترع أنيميشن تويكل جديد من الصفر
    val sparkleAlphas = SummerTheme.rememberParticleAlphas(4)
    val sparkleSpots = remember {
        listOf(
            Triple(Alignment.TopStart, 10.dp, (-6).dp),
            Triple(Alignment.TopStart, 46.dp, 10.dp),
            Triple(Alignment.TopEnd, (-14).dp, (-2).dp),
            Triple(Alignment.TopEnd, (-48).dp, 12.dp)
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isUnlocked) {
                        Brush.verticalGradient(listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep))
                    } else {
                        Brush.verticalGradient(listOf(SummerTheme.colors.chestLockedStart, SummerTheme.colors.chestLockedEnd))
                    }
                )
                .clickable(enabled = isUnlocked) { onClick() }
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // الصندوق بقى العنصر الأساسي البارز في الكارت (مش أيقونة صغيرة جوه
            // مربع)، ده اللي كانت طلبته: "يظهر كبير كدا مقفول"
            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                if (isUnlocked) {
                    // هالة دايرة بتلف ببطء وراء الصندوق وهو مفتوح
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .rotate(glowAngle)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        SummerTheme.colors.transparent,
                                        SummerTheme.colors.white.copy(alpha = 0.5f),
                                        SummerTheme.colors.transparent,
                                        SummerTheme.colors.transparent
                                    )
                                ),
                                RoundedCornerShape(50)
                            )
                    )
                } else if (totalCount > 0) {
                    // شريط تقدّم دائري حوالين الصندوق نفسه — بيوضّح "قربتي قد إيه"
                    // بدل ما الحالة تبقى إما مقفول أو مفتوح من غير أي إحساس بالتقدّم
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(140.dp),
                        color = SummerTheme.colors.goldWarm,
                        trackColor = SummerTheme.colors.white.copy(alpha = 0.18f),
                        strokeWidth = 5.dp,
                        strokeCap = StrokeCap.Round
                    )
                }

                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .scale(breathe)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SummerTheme.colors.white.copy(alpha = if (isUnlocked) 0.2f else 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { chestAnimatable.value },
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
            }

            Text(
                text = if (isUnlocked) genderedStringResource(GenderedStrings.treasureUnlocked) else genderedStringResource(GenderedStrings.treasureCompleteTasks),
                style = SummerTheme.typography.chestTitle,
                color = if (isUnlocked) SummerTheme.colors.white else SummerTheme.colors.chestLockedText,
                modifier = Modifier.padding(top = 14.dp)
            )

            if (!isUnlocked && totalCount > 0) {
                Text(
                    text = genderedStringResource(GenderedStrings.treasureProgress, completedCount, totalCount),
                    style = SummerTheme.typography.chestSubtitle,
                    color = SummerTheme.colors.chestLockedText.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(
                text = if (isUnlocked) genderedStringResource(GenderedStrings.treasureOpen) else genderedStringResource(GenderedStrings.treasureUnlockHint),
                style = SummerTheme.typography.chestSubtitle,
                color = if (isUnlocked) SummerTheme.colors.white.copy(alpha = 0.85f) else SummerTheme.colors.chestLockedText.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        sparkleSpots.forEachIndexed { index, (alignment, x, y) ->
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = if (isUnlocked) {
                    SummerTheme.colors.white.copy(alpha = sparkleAlphas[index] * 0.9f)
                } else {
                    SummerTheme.colors.goldWarm.copy(alpha = sparkleAlphas[index] * 0.5f)
                },
                modifier = Modifier
                    .align(alignment)
                    .offset(x = x, y = y)
                    .size(12.dp)
            )
        }
    }
}