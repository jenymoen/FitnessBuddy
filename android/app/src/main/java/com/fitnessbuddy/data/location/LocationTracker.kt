package com.fitnessbuddy.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val speed: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L // Update every 1 second
    ).apply {
        setMinUpdateIntervalMillis(500L)
        setMinUpdateDistanceMeters(2f) // Minimum 2 meters between updates
    }.build()

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<LocationPoint> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(
                        LocationPoint(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            altitude = location.altitude,
                            speed = location.speed,
                            timestamp = location.time
                        )
                    )
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationPoint? {
        return try {
            val location = fusedLocationClient.lastLocation.result
            location?.let {
                LocationPoint(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude,
                    speed = it.speed,
                    timestamp = it.time
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun calculateDistance(points: List<LocationPoint>): Float {
            if (points.size < 2) return 0f
            var totalDistance = 0f
            for (i in 1 until points.size) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    points[i - 1].latitude,
                    points[i - 1].longitude,
                    points[i].latitude,
                    points[i].longitude,
                    results
                )
                totalDistance += results[0]
            }
            return totalDistance
        }

        fun calculatePace(distanceMeters: Float, durationSeconds: Long): String {
            if (distanceMeters <= 0 || durationSeconds <= 0) return "--:--"
            val distanceKm = distanceMeters / 1000f
            val paceSecondsPerKm = durationSeconds / distanceKm
            val minutes = (paceSecondsPerKm / 60).toInt()
            val seconds = (paceSecondsPerKm % 60).toInt()
            return String.format("%d:%02d", minutes, seconds)
        }
    }
}
