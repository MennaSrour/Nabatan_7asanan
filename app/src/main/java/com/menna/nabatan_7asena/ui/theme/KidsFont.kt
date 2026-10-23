package com.menna.nabatan_7asena.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.R

/**
 * خط "Baloo Bhaijaan 2" — خط عربي مستدير ومرح مصمم أصلاً لواجهات
 * الأطفال، بعكس الخط الافتراضي (Roboto) اللي شكله رسمي/جدي أكتر من
 * اللازم لتطبيق أطفال.
 *
 * ⚠️ خطوة يدوية مطلوبة منك: الخط ده مش لازم يتحط جوه المشروع، نزّليه
 * (مجاني) من هنا:
 * https://fonts.google.com/specimen/Baloo+Bhaijaan+2
 * وحطي الملفات في app/src/main/res/font/ بالأسامي دي بالظبط (lowercase
 * وunderscore، ده شرط تسمية الـ resources في أندرويد):
 *   - baloo_bhaijaan2_regular.ttf     (400)
 *   - baloo_bhaijaan2_medium.ttf      (500)
 *   - baloo_bhaijaan2_semibold.ttf    (600)
 *   - baloo_bhaijaan2_bold.ttf        (700)
 *   - baloo_bhaijaan2_extrabold.ttf   (800)
 *
 * لو مستعجلة وعاوزة تجربي الشكل فورًا من غير ما تدوّري على الخط، سيبي
 * الفايل ده زي ما هو مؤقتًا واستبدلي كل fontFamily = KidsFontFamily
 * بـ FontFamily.Default في الشاشات اللي هنعدلها، وبعدين رجّعيها لما
 * تجهزي الخط.
 */
val KidsFontFamily = FontFamily(
    Font(R.font.baloo_bhaijaan2_regular, FontWeight.Normal),
    Font(R.font.baloo_bhaijaan2_medium, FontWeight.Medium),
    Font(R.font.baloo_bhaijaan2_semibold, FontWeight.SemiBold),
    Font(R.font.baloo_bhaijaan2_bold, FontWeight.Bold),
    Font(R.font.baloo_bhaijaan2_extrabold, FontWeight.ExtraBold)
)