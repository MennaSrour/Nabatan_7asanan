package com.menna.nabata_7asena.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class Typography(
    val bottomSheetTitle: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
    ),
    val chipText: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    ),
    val taskTitle: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val overlayTitle: TextStyle = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Black
    ),
    val overlaySubtitle: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    ),
    val dialogTitle: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Black
    ),
    val dialogQuestion: TextStyle = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp
    ),
    val buttonText: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    val wisdomTitle: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    ),
    val wisdomBody: TextStyle = TextStyle(
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 21.sp
    ),
    val statValue: TextStyle = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Black
    ),
    val statLabelPrimary: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    ),
    val statLabelSecondary: TextStyle = TextStyle(
        fontSize = 10.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val headerWelcome: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
    ),
    val headerDate: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    ),
    val prayerTimeNext: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTimeNormal: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTitleNext: TextStyle = TextStyle(
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Black
    ),
    val prayerTitleNormal: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Black
    ),
    val taskSubtitle: TextStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val chestTitle: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    ),
    val chestSubtitle: TextStyle = TextStyle(
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    val sectionTitle: TextStyle = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Black
    ),
    val addButtonText: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    ),
    val snackbarText: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    ),

)

val LocalSummerTypography = staticCompositionLocalOf { Typography() }