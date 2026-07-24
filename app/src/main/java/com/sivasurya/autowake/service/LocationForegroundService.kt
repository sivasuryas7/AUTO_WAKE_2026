package com.sivasurya.autowake.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
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
        android.util.Log.d(
            "AUTO_WAKE",
            "Service Created"
        )
        super.onCreate()

        // Create notification channel first
        NotificationHelper.createNotificationChannel(this)


        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)


        locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    for(location in result.locations){

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
                .setContentText(
                    "Monitoring your destination..."
                )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
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

        android.util.Log.d(
            "AUTO_WAKE",
            "Service Started"
        )
        destinationLatitude =
            intent?.getDoubleExtra(
                "latitude",
                0.0
            ) ?: 0.0



        destinationLongitude =
            intent?.getDoubleExtra(
                "longitude",
                0.0
            ) ?: 0.0



        if(
            destinationLatitude != 0.0 &&
            destinationLongitude != 0.0
        ){

            startLocationUpdates()

        }
        else{

            stopSelf()

        }



        return START_STICKY
    }





    private fun startLocationUpdates(){


        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000
            )
                .setMinUpdateDistanceMeters(5f)
                .build()



        if(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ){

            stopSelf()
            return

        }



        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )

    }





    private fun checkDestination(
        currentLocation: Location
    ){


        val destination =
            Location("destination")



        destination.latitude =
            destinationLatitude


        destination.longitude =
            destinationLongitude



        val distance =
            currentLocation.distanceTo(destination)


        val distanceIntent = Intent("DISTANCE_UPDATE").apply {
            putExtra("distance", distance)
        }

        sendBroadcast(distanceIntent)
        if(distance <= destinationRadius){

            destinationReached()

        }

    }





    private fun destinationReached() {

        // Stop receiving location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Start alarm and vibration
        AlarmHelper.playAlarm(this)
        VibrationHelper.start(this)

        // Stop location tracking service
        stopSelf()
    }

    override fun onDestroy() {

        android.util.Log.d(
            "AUTO_WAKE",
            "Service Destroyed"
        )


        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )


        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null

    }

}