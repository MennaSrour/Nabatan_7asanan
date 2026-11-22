package com.menna.nabatan_7asena.core.config

enum class SeasonalThemeOverride { AUTO, FORCE_RAMADAN, FORCE_EID, NONE }

object SeasonalThemeConfig {
    val override: SeasonalThemeOverride = SeasonalThemeOverride.AUTO
}
