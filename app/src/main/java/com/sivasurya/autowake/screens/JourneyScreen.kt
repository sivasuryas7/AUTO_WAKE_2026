package com.sivasurya.autowake.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sivasurya.autowake.service.LocationForegroundService
import android.os.Build
import com.sivasurya.autowake.helpers.AlarmHelper
import com.sivasurya.autowake.helpers.VibrationHelper
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.Context
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

@Composable
fun JourneyScreen(latitude: Double,
                  longitude: Double) {


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

            if(isGranted){
                message = "Notification Permission Granted"
            }
            else{
                message = "Notification Permission Denied"
            }

        }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if(isGranted){

                message =
                    "Permission Granted. Press Start Journey Again."

            }
            else{

                message =
                    "Location Permission Denied"

            }

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
                filter
            )
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }

    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = "Journey Ready",
            fontSize = 30.sp
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Selected Destination",
            fontSize = 20.sp
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Destination Latitude : $latitude"
        )

        Text(
            text = "Destination Longitude : $longitude"
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = message,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = String.format(
                "Remaining Distance : %.2f km",
                remainingDistance / 1000
            ),
            fontSize = 20.sp
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )



        Button(

            onClick = {


                if(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ){


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



                    if(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ){

                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )

                    }
                    else{


                        ContextCompat.startForegroundService(
                            context,
                            serviceIntent
                        )


                        message =
                            "Journey Started"

                    }


                }
                else{


                    permissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )


                }


            },

            modifier = Modifier.fillMaxWidth()

        ){

            Text("Start Journey")

        }
        Button(
            onClick = {

                AlarmHelper.stopAlarm()
                VibrationHelper.stop()

                message = "Alarm Stopped"

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Stop Alarm")

        }



        Spacer(
            modifier = Modifier.height(16.dp)
        )




        OutlinedButton(

            onClick = {


                context.stopService(
                    Intent(
                        context,
                        LocationForegroundService::class.java
                    )
                )


                message =
                    "Journey Stopped"


            },

            modifier = Modifier.fillMaxWidth()

        ){

            Text("Stop Journey")

        }


    }

}