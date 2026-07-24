package com.sivasurya.autowake.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*

class LocationHelper(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        interval: Long = 5000L,
        priority: Int = Priority.PRIORITY_HIGH_ACCURACY,
        onLocationReceived: (Double, Double) -> Unit
    ) {

        val locationRequest =
            LocationRequest.Builder(priority, interval)
                .setMinUpdateIntervalMillis(interval / 2)
                .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                locationResult.lastLocation?.let {

                    onLocationReceived(
                        it.latitude,
                        it.longitude
                    )

                }

            }

        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        )
    }

    fun stopLocationUpdates() {

        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

        locationCallback = null
    }
}