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

internal object IosLocationAccess : LocationTracker {
    private val mutex = Mutex()
    private val authorizationMutex = Mutex()
    private var authorization: CancellableContinuation<Unit>? = null
    private var position: CancellableContinuation<CLLocation?>? = null
    private val locationDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
            if (didChangeAuthorizationStatus != kCLAuthorizationStatusNotDetermined) {
                val pending = authorization
                authorization = null
                pending?.takeIf { it.isActive }?.resume(Unit)
            }
        }
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val pending = position
            position = null
            val fix = (didUpdateLocations.lastOrNull() as? CLLocation)?.takeIf {
                it.horizontalAccuracy >= 0.0 && kotlin.math.abs(it.timestamp.timeIntervalSinceNow) < 60.0
            }
            pending?.takeIf { it.isActive }?.resume(fix)
        }
        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            val pending = position
            position = null
            pending?.takeIf { it.isActive }?.resume(null)
        }
    }
    private val manager = CLLocationManager().apply { delegate = locationDelegate }

    fun granted(): Boolean = CLLocationManager.locationServicesEnabled() &&
        CLLocationManager.authorizationStatus() in listOf(kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse)
    suspend fun requestAuthorization() = withContext(Dispatchers.Main) {
        authorizationMutex.withLock {
            if (CLLocationManager.authorizationStatus() != kCLAuthorizationStatusNotDetermined) return@withLock
            withTimeoutOrNull(60_000) {
                suspendCancellableCoroutine<Unit> { continuation ->
                    authorization = continuation
                    continuation.invokeOnCancellation {
                        platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                            if (authorization === continuation) authorization = null
                        }
                    }
                    manager.requestWhenInUseAuthorization()
                }
            }
            Unit
        }
    }
    override suspend fun getCurrentLocation(): UserLocation? = withContext(Dispatchers.Main) {
        mutex.withLock {
            if (!granted()) return@withLock null
            val location = withTimeoutOrNull(10_000) {
                suspendCancellableCoroutine<CLLocation?> { continuation ->
                    position = continuation
                    continuation.invokeOnCancellation {
                        platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                            if (position === continuation) { position = null; manager.stopUpdatingLocation() }
                        }
                    }
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
