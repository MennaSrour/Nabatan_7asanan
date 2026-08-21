package com.menna.nabata_7asena.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SummerColors(
    val backgroundLight: Color = Color(0xFFF9FBE7),
    val goldWarm: Color = Color(0xFFFFC83D),
    val goldDeep: Color = Color(0xFFFF9F1C),
    val textPrimary: Color = Color(0xFF37474F),
    val textBrown: Color = Color(0xFF6D4C41),
    val textDarkBrown: Color = Color(0xFF3E2723),
    val chipUnselected: Color = Color(0xFFEFE8D5),
    val cardBackground: Color = Color.White,
    val transparent: Color = Color.Transparent,
    val white: Color = Color.White,
    val overlayDark: Color = Color(0xFF071A52),
    val dialogBackground: Color = Color(0xFFFFFDE7),
    val errorRed: Color = Color(0xFFEF5350),
    val successGreen: Color = Color(0xFF22C55E),

    val wisdomStart: Color = Color(0xFFfcf8f7),
    val wisdomEnd: Color = Color(0xFFEFE6FA),
    val wisdomAccent: Color = Color(0xFF7A5FD1),
    val wisdomTextPrimary: Color = Color(0xFF373e5a),
    val wisdomTextSecondary: Color = Color(0xFF5e6074),

    val statGreenStart: Color = Color(0xFF95ddbb),
    val statGreenEnd: Color = Color(0xFF5cd3d1),
    val statPurpleStart: Color = Color(0xFFb28ee9),
    val statPurpleEnd: Color = Color(0xFF5cd3d1),

    val headerTextDark: Color = Color(0xFF04000E),
    val settingsIconDark: Color = Color(0xFF41464D),
    val taskCompletedBase: Color = Color(0xFFEDEBF4),

// ألوان الصلوات (يمكن وضعها هنا لتسهيل الوصول)
    val fajrStart: Color = Color(0xFF1E2A5C),
    val fajrMid: Color = Color(0xFF4B4F92),
    val fajrEnd: Color = Color(0xFFB088A8),
    val fajrAccent: Color = Color(0xFF4B3F86),
// ألوان بطاقة الكنز المغلقة
    val chestLockedStart: Color = Color(0xFFDCD8EC),
    val chestLockedEnd: Color = Color(0xFFC7C1E0),
    val chestLockedText: Color = Color(0xFF5B5470),


    val dayBackground: Color = Color(0xFFfcf7f1),

    val sectionTitleDark: Color = Color(0xFF071A52),
    val addBtnBorder: Color = Color(0xFF22C55E).copy(alpha = 0.4f),
    val addBtnShadow: Color = Color(0xFF22C55E).copy(alpha = 0.15f),
    val addBtnText: Color = Color(0xFF071A52),
    )

val LocalSummerColors = staticCompositionLocalOf { SummerColors() }