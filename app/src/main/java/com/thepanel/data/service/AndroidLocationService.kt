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
            trySend(LocationState(error = "Konum izni verilmedi"))
            close()
            return@callbackFlow
        }

        val geocoder = Geocoder(context, Locale("tr"))
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
        val address = runCatching {
            geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
        }.getOrNull()
        return LocationState(
            available = true,
            country = address?.countryName.orEmpty(),
            province = address?.adminArea.orEmpty(),
            district = address?.subAdminArea ?: address?.locality.orEmpty(),
            latitude = formatCoordinate(latitude),
            longitude = formatCoordinate(longitude),
            speed = formatSpeedMps(speed),
            heading = formatHeading(bearing),
            accuracy = formatAccuracy(accuracy),
            altitude = formatAltitude(if (hasAltitude()) altitude else null),
            error = null
        )
    }
}
