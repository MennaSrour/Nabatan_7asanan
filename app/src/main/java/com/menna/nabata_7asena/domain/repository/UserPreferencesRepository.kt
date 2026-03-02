//package com.menna.nabata_7asena.domain.repository
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.core.content.ContextCompat
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class UserPreferencesRepository @Inject constructor(
//    @ApplicationContext private val context: Context
//) {
//    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//
//    fun isOnboardingCompleted() = prefs.getBoolean("is_onboarding_completed", false)
//
//    fun saveOnboardingCompleted() = prefs.edit().putBoolean("is_onboarding_completed", true).apply()
//
//    fun arePermissionsGranted(): Boolean {
//        val location = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val notif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        } else true
//
//        return location && notif
//    }
//}