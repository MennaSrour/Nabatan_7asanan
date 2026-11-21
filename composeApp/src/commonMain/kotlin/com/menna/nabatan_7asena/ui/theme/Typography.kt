package com.menna.nabatan_7asena.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

data class Typography(
    val fontFamily: FontFamily? = null,
    val bottomSheetTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
    ),
    val chipText: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    ),
    val taskTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val overlayTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 30.sp,
        fontWeight = FontWeight.Black
    ),
    val overlaySubtitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    ),
    val dialogTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black
    ),
    val dialogQuestion: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp
    ),
    val buttonText: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    val wisdomTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    ),
    val wisdomBody: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 21.sp
    ),
    val statValue: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Black
    ),
    val statLabelPrimary: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    ),
    val statLabelSecondary: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val headerWelcome: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
    ),
    val headerDate: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    ),
    val prayerTimeNext: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTimeNormal: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTitleNext: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTitleNormal: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black
    ),
    val taskSubtitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val chestTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    ),
    val chestSubtitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val sectionTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.Black
    ),
    val addButtonText: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    ),
    val snackbarText: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    ),

)

val LocalSummerTypography = staticCompositionLocalOf { Typography() }