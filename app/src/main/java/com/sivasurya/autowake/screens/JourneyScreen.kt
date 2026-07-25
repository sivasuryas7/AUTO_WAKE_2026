package com.sivasurya.autowake.screens

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sivasurya.autowake.helpers.AlarmHelper
import com.sivasurya.autowake.helpers.JourneyState
import com.sivasurya.autowake.helpers.VibrationHelper
import com.sivasurya.autowake.service.LocationForegroundService


@Composable
fun JourneyScreen(
    latitude: Double,
    longitude: Double
) {

    val context = LocalContext.current

    var remainingDistance by remember {
        mutableStateOf(0f)
    }

    var message by remember {
        mutableStateOf("Press Start Journey")
    }


    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            message =
                if (isGranted)
                    "Notification Permission Granted"
                else
                    "Notification Permission Denied"

        }



    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            message =
                if (isGranted)
                    "Permission Granted. Press Start Journey Again."
                else
                    "Location Permission Denied"

        }



    DisposableEffect(Unit) {

        val receiver = object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                remainingDistance =
                    intent?.getFloatExtra(
                        "distance",
                        0f
                    ) ?: 0f


                android.util.Log.d(
                    "AUTO_WAKE",
                    "Distance = $remainingDistance"
                )

            }

        }



        val filter = IntentFilter("DISTANCE_UPDATE")



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            context.registerReceiver(
                receiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )

        } else {

            @Suppress("DEPRECATION")
            context.registerReceiver(
                receiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )

        }



        onDispose {

            context.unregisterReceiver(receiver)

        }

    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {


        Text(
            text = "Journey Tracking",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )


        Text(
            text = "AUTO WAKE is monitoring your journey.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(modifier = Modifier.height(20.dp))



        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Destination",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Latitude : $latitude")
                Text("Longitude : $longitude")

            }

        }



        Spacer(modifier = Modifier.height(16.dp))



        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Remaining Distance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    String.format(
                        "%.2f km",
                        remainingDistance / 1000
                    ),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }



        Spacer(modifier = Modifier.height(16.dp))



        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Status",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(message)

            }

        }



        Spacer(modifier = Modifier.weight(1f))



        Button(
            onClick = {

                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {


                    val serviceIntent =
                        Intent(
                            context,
                            LocationForegroundService::class.java
                        ).apply {

                            putExtra(
                                "latitude",
                                latitude
                            )

                            putExtra(
                                "longitude",
                                longitude
                            )

                            putExtra(
                                "destination_name",
                                "Selected Destination"
                            )

                        }



                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {


                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )


                    } else {


                        ContextCompat.startForegroundService(
                            context,
                            serviceIntent
                        )


                        JourneyState.isJourneyActive = true

                        JourneyState.destinationName =
                            "Selected Destination"


                        message = "Journey Started"

                    }


                } else {

                    permissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                }

            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(16.dp)

        ) {

            Text("Start Journey")

        }



        Spacer(modifier = Modifier.height(12.dp))



        Button(
            onClick = {

                AlarmHelper.stopAlarm()
                VibrationHelper.stop()

                message = "Alarm Stopped"

            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(16.dp)

        ) {

            Text("Stop Alarm")

        }



        Spacer(modifier = Modifier.height(12.dp))



        OutlinedButton(
            onClick = {

                context.stopService(
                    Intent(
                        context,
                        LocationForegroundService::class.java
                    )
                )

                JourneyState.isJourneyActive = false
                JourneyState.destinationName = ""
                message = "Journey Stopped"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Stop Journey")
        }
    }
}