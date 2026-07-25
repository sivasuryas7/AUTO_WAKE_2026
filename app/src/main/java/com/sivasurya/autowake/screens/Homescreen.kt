package com.sivasurya.autowake.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.sivasurya.autowake.helpers.JourneyState
import com.sivasurya.autowake.navigation.Screen


@Composable
fun HomeScreen(
    navController: NavHostController
) {

    val context = LocalContext.current

    var currentLocation by remember {
        mutableStateOf("Fetching...")
    }


    // Observe journey state
    val isJourneyActive = JourneyState.isJourneyActive



    // Fetch current GPS location
    LaunchedEffect(Unit) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)


        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {

                        currentLocation =
                            "${location.latitude}, ${location.longitude}"

                    } else {

                        currentLocation = "Location unavailable"

                    }

                }

        } else {

            currentLocation = "Permission required"

        }

    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {


        Column {


            Spacer(modifier = Modifier.height(20.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {


                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )


                Spacer(modifier = Modifier.width(12.dp))


                Column {


                    Text(
                        text = "AUTO WAKE",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )


                    Text(
                        text = "Rise at the Right Destination",
                        color = Color.Gray
                    )

                }

            }



            Spacer(modifier = Modifier.height(30.dp))



            // Current Location Card

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )


                    Spacer(modifier = Modifier.width(12.dp))


                    Column {


                        Text(
                            "Current Location",
                            color = Color.Gray
                        )


                        Text(
                            currentLocation,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )


                    }


                }


            }



            Spacer(modifier = Modifier.height(20.dp))



            // Destination Card

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {


                Column(
                    modifier = Modifier.padding(18.dp)
                ) {


                    Text(
                        "Destination",
                        fontWeight = FontWeight.SemiBold
                    )


                    Spacer(modifier = Modifier.height(10.dp))


                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.Destination.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {


                        Icon(
                            Icons.Default.Place,
                            contentDescription = null
                        )


                        Spacer(modifier = Modifier.width(8.dp))


                        Text(
                            "Choose Destination"
                        )


                    }


                }


            }



            // Ongoing Journey Card

            if (isJourneyActive) {


                Spacer(modifier = Modifier.height(20.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {


                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {


                        Text(
                            "🚍 Ongoing Journey",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )


                        Spacer(modifier = Modifier.height(10.dp))


                        Text(
                            "Destination",
                            color = Color.Gray
                        )


                        Text(
                            JourneyState.destinationName.ifEmpty {
                                "Selected Destination"
                            },
                            fontWeight = FontWeight.Bold
                        )


                        Spacer(modifier = Modifier.height(8.dp))


                        Text(
                            "📍 Tracking location...",
                            color = MaterialTheme.colorScheme.primary
                        )


                        Spacer(modifier = Modifier.height(12.dp))


                        OutlinedButton(
                            onClick = {
                                navController.navigate(Screen.Journey.route)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text("View Journey")

                        }


                    }


                }


            }


        }



        Button(
            onClick = {

                navController.navigate(Screen.Destination.route)

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {


            Icon(
                Icons.Default.Navigation,
                contentDescription = null
            )


            Spacer(modifier = Modifier.width(8.dp))


            Text(
                "Start Journey",
                fontSize = 18.sp
            )


        }


    }

}