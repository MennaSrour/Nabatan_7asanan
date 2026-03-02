package com.menna.nabata_7asena.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.menna.nabata_7asena.domain.LocationTracker
import com.menna.nabata_7asena.domain.entity.UserLocation 
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class LocationTrackerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationTracker {

    override suspend fun getCurrentLocation(): UserLocation? { 
        val hasAccess = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!hasAccess || !isGpsEnabled) {
            Log.w("LocationTracker", "GPS مقفول أو مفيش صلاحية")
            return null
        }

        return try {
            var location = client.lastLocation.await()
            if (location == null) {
                location = client.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
                ).await()
            }

            if (location != null) {
                val geocoder = Geocoder(context, Locale("en")) 
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]

                    
                    val city = address.subAdminArea ?: address.locality ?: address.adminArea ?: "Cairo"

                    
                    val country = address.countryName ?: "Egypt"

                    Log.d("LocationTracker", "تم تحديد الموقع: $city, $country")

                    
                    UserLocation(
                        city = city,
                        country = country,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}