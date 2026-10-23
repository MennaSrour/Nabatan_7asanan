package com.menna.nabatan_7asena.data.local

import android.content.Context
import android.content.SharedPreferences
import com.menna.nabatan_7asena.domain.repository.FirstLaunchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirstLaunchManager @Inject constructor(
    @ApplicationContext context: Context
) : FirstLaunchRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean {
        
        return prefs.getBoolean("is_first_launch", true)
    }

    fun setFirstTimeFinished() {
        prefs.edit().putBoolean("is_first_launch", false).apply()
    }

    
    override fun resetToFirstLaunch() {
        
        prefs.edit().clear().apply()
    }
}