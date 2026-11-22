@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.platform

import com.menna.nabatan_7asena.domain.LocationTracker
import com.menna.nabatan_7asena.domain.entity.UserLocation
import kotlinx.cinterop.useContents
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.CoreLocation.*
import platform.Foundation.*
import platform.darwin.NSObject
import kotlin.coroutines.resume

internal object IosLocationAccess : NSObject(), CLLocationManagerDelegateProtocol, LocationTracker {
    private val manager = CLLocationManager().apply { delegate = this@IosLocationAccess }
    private val mutex = Mutex()
    private var authorization: CancellableContinuation<Unit>? = null
    private var position: CancellableContinuation<CLLocation?>? = null
    fun granted(): Boolean = CLLocationManager.locationServicesEnabled() &&
        CLLocationManager.authorizationStatus() in listOf(kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse)
    suspend fun requestAuthorization() {
        if (CLLocationManager.authorizationStatus() != kCLAuthorizationStatusNotDetermined) return
        suspendCancellableCoroutine { continuation ->
            authorization = continuation
            continuation.invokeOnCancellation { authorization = null }
            manager.requestWhenInUseAuthorization()
        }
    }
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        if (didChangeAuthorizationStatus != kCLAuthorizationStatusNotDetermined) {
            authorization?.takeIf { it.isActive }?.resume(Unit)
            authorization = null
        }
    }
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        position?.takeIf { it.isActive }?.resume(didUpdateLocations.lastOrNull() as? CLLocation)
        position = null
    }
    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        position?.takeIf { it.isActive }?.resume(null)
        position = null
    }
    override suspend fun getCurrentLocation(): UserLocation? = withContext(Dispatchers.Main) {
        mutex.withLock {
            if (!granted()) return@withLock null
            val location = withTimeoutOrNull(10_000) {
                suspendCancellableCoroutine<CLLocation?> { continuation ->
                    position = continuation
                    continuation.invokeOnCancellation { position = null; manager.stopUpdatingLocation() }
                    manager.requestLocation()
                }
            } ?: return@withLock null
            val placemark = withTimeoutOrNull(5_000) {
                suspendCancellableCoroutine<CLPlacemark?> { continuation ->
                    val geocoder = CLGeocoder()
                    continuation.invokeOnCancellation { geocoder.cancelGeocode() }
                    geocoder.reverseGeocodeLocation(location) { places, _ ->
                        if (continuation.isActive) continuation.resume(places?.firstOrNull() as? CLPlacemark)
                    }
                }
            } ?: return@withLock null
            val code = placemark.ISOcountryCode ?: return@withLock null
            val city = placemark.locality ?: placemark.administrativeArea ?: placemark.country ?: code
            location.coordinate.useContents {
                UserLocation(city, placemark.country ?: code, latitude, longitude, code,
                    placemark.timeZone?.name ?: kotlinx.datetime.TimeZone.currentSystemDefault().id)
            }
        }
    }
}
