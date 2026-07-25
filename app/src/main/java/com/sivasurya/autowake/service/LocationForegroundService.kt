package com.sivasurya.autowake.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.sivasurya.autowake.R
import com.sivasurya.autowake.helpers.NotificationHelper
import com.sivasurya.autowake.helpers.AlarmHelper
import com.sivasurya.autowake.helpers.VibrationHelper

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var destinationLatitude = 0.0
    private var destinationLongitude = 0.0

    private val destinationRadius = 100f // meters

    override fun onCreate() {

        super.onCreate()

        Log.d("AUTO_WAKE", "Service Created")

        NotificationHelper.createNotificationChannel(this)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(result: LocationResult) {

                    Log.d(
                        "AUTO_WAKE",
                        "Received ${result.locations.size} location update(s)"
                    )

                    for (location in result.locations) {

                        Log.d(
                            "AUTO_WAKE",
                            "Current Location -> Lat: ${location.latitude}, Lon: ${location.longitude}"
                        )

                        checkDestination(location)
                    }
                }
            }

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                NotificationHelper.CHANNEL_ID
            )
                .setContentTitle("AUTO WAKE")
                .setContentText("Monitoring your destination...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build()

        startForeground(
            1001,
            notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("AUTO_WAKE", "Service Started")

        destinationLatitude =
            intent?.getDoubleExtra("latitude", 0.0) ?: 0.0

        destinationLongitude =
            intent?.getDoubleExtra("longitude", 0.0) ?: 0.0

        Log.d(
            "AUTO_WAKE",
            "Destination -> Lat: $destinationLatitude, Lon: $destinationLongitude"
        )

        if (
            destinationLatitude != 0.0 &&
            destinationLongitude != 0.0
        ) {

            startLocationUpdates()

        } else {

            Log.d("AUTO_WAKE", "Invalid destination. Stopping service.")
            stopSelf()

        }

        return START_STICKY
    }

    private fun startLocationUpdates() {

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000
            )
                .setMinUpdateDistanceMeters(5f)
                .build()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.d("AUTO_WAKE", "Location permission NOT granted")

            stopSelf()
            return
        }

        Log.d("AUTO_WAKE", "Starting location updates...")

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun checkDestination(
        currentLocation: Location
    ) {

        val destination = Location("destination").apply {
            latitude = destinationLatitude
            longitude = destinationLongitude
        }

        val distance = currentLocation.distanceTo(destination)

        Log.d(
            "AUTO_WAKE",
            "Distance to destination = $distance meters"
        )

        val distanceIntent = Intent().apply {
            action = "DISTANCE_UPDATE"
            setPackage(packageName)
            putExtra("distance", distance)
        }

        sendBroadcast(distanceIntent)

        sendBroadcast(distanceIntent)

        Log.d("AUTO_WAKE", "Distance broadcast sent")

        if (distance <= destinationRadius) {

            Log.d("AUTO_WAKE", "Destination Reached!")

            destinationReached()
        }
    }

    private fun destinationReached() {

        fusedLocationClient.removeLocationUpdates(locationCallback)

        AlarmHelper.playAlarm(this)
        VibrationHelper.start(this)

        stopSelf()
    }

    override fun onDestroy() {

        Log.d("AUTO_WAKE", "Service Destroyed")

        fusedLocationClient.removeLocationUpdates(locationCallback)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}