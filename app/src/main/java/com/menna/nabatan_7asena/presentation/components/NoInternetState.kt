package com.menna.nabatan_7asena.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

/**
 * حالة "مفيش إنترنت" عامة — قابلة للاستخدام في أي مكان في التطبيق فشل فيه تحميل
 * حاجة معينة بسبب النت (مش بس الصلوات)، بدل ما القسم يفضل فاضي بصمت من غير أي
 * تفسير. زي ما اتفقنا، العنوان والزرار قابلين للتخصيص عشان تتظبط على أي سياق
 * ("تعذّر تحميل مواقيت الصلاة"، "تعذّر تحميل الاقتراحات"...) من غير ما نكرر
 * الكومبوننت دي في كل شاشة.
 */
@Composable
fun NoInternetState(
    modifier: Modifier = Modifier,
    title: String = genderedStringResource(GenderedStrings.internetRequired),
    subtitle: String = genderedStringResource(GenderedStrings.internetRetryHint),
    onRetry: (() -> Unit)? = null,
    // مقاس مضغوط لما بنستخدمها بدل قسم واحد بس (زي صف الصلوات) مش الشاشة كلها،
    // عشان ميبقاش عندنا رسمة كبيرة قد الشاشة مكان صف كان طوله سطرين
    compact: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = if (compact) 12.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_internet))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(if (compact) 64.dp else 130.dp)
        )

        Spacer(Modifier.height(if (compact) 4.dp else 8.dp))

        Text(
            text = title,
            style = if (compact) SummerTheme.typography.taskSubtitle else SummerTheme.typography.taskTitle,
            color = SummerTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        if (!compact) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = SummerTheme.typography.taskSubtitle,
                color = SummerTheme.colors.textBrown,
                textAlign = TextAlign.Center
            )
        }

        if (onRetry != null) {
            Spacer(Modifier.height(if (compact) 10.dp else 16.dp))
            Surface(
                onClick = onRetry,
                shape = RoundedCornerShape(50),
                color = SummerTheme.colors.goldWarm
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = if (compact) 16.dp else 22.dp,
                        vertical = if (compact) 8.dp else 11.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = SummerTheme.colors.white,
                        modifier = Modifier.size(if (compact) 15.dp else 18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = genderedStringResource(GenderedStrings.retry),
                        style = SummerTheme.typography.buttonText,
                        color = SummerTheme.colors.white
                    )
                }
            }
        }
    }
}