package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.platform.PlatformPreferences
import com.menna.nabatan_7asena.domain.repository.FirstLaunchRepository

class FirstLaunchManager : FirstLaunchRepository {
    private val prefs = PlatformPreferences("app_prefs")
    fun isFirstTime() = prefs.getBoolean("is_first_launch", true)
    fun setFirstTimeFinished() = prefs.putBoolean("is_first_launch", false)
    override fun resetToFirstLaunch() = prefs.clear()
}
