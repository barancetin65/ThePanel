package com.thepanel.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.thepanel.data.model.LocationState
import com.thepanel.data.util.formatAccuracy
import com.thepanel.data.util.formatAltitude
import com.thepanel.data.util.formatCoordinate
import com.thepanel.data.util.formatHeading
import com.thepanel.data.util.formatSpeedMps
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AndroidLocationService(
    private val context: Context
) : LocationService {
    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @SuppressLint("MissingPermission")
    override fun liveLocation(): Flow<LocationState> = callbackFlow {
        if (!hasLocationPermission()) {
            trySend(LocationState(error = "Location permission not granted"))
            close()
            return@callbackFlow
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                scope.launch {
                    trySend(location.toLocationState(geocoder))
                }
            }
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()
        client.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose { client.removeLocationUpdates(callback) }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private suspend fun Location.toLocationState(geocoder: Geocoder): LocationState {
        val addresses = runCatching {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(latitude, longitude, 3)
        }.getOrNull() ?: emptyList()

        // Pick the best match: prefer one with locality or subLocality
        val address = addresses.find { it.locality != null }
            ?: addresses.find { it.subLocality != null }
            ?: addresses.firstOrNull()

        // Fallback or secondary check: if best match is still missing district info, try others
        val district = address?.let { addr ->
            addr.locality ?: addr.subLocality ?: addr.subAdminArea ?: addr.adminArea
        } ?: addresses.firstOrNull()?.let { addr ->
            addr.locality ?: addr.subLocality ?: addr.subAdminArea ?: addr.adminArea
        }.orEmpty()

        val province = address?.adminArea ?: addresses.firstOrNull()?.adminArea.orEmpty()
        val country = address?.countryName ?: addresses.firstOrNull()?.countryName.orEmpty()

        return LocationState(
            available = true,
            country = country,
            province = province,
            district = district,
            latitude = formatCoordinate(latitude),
            longitude = formatCoordinate(longitude),
            speed = formatSpeedMps(speed),
            heading = formatHeading(bearing),
            accuracy = formatAccuracy(accuracy),
            altitude = formatAltitude(if (hasAltitude()) altitude else null),
            error = if (addresses.isEmpty()) "Geocoder returned no address" else null
        )
    }
}
