package com.sivasurya.autowake.screens

import com.sivasurya.autowake.model.SelectedPlace
import com.sivasurya.autowake.navigation.Screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sivasurya.autowake.model.Place
import kotlinx.coroutines.launch

@Composable
fun DestinationScreen(navController: NavController) {

    var searchText by remember { mutableStateOf("") }
    var places by remember { mutableStateOf<List<Place>>(emptyList()) }

    val repository =
        remember {
            com.sivasurya.autowake.repository.SearchRepository()
        }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Search Destination",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Enter Place")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                scope.launch {

                    try {

                        places =
                            repository.search(searchText)

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }

                }

            }
        ) {

            Text("Search")

        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                navController.navigate(
                    Screen.Map.route
                )

            }
        ) {

            Text("🗺 Select Destination From Map")

        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(places) { place ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {

                            SelectedPlace.name =
                                place.display_name

                            SelectedPlace.latitude =
                                place.lat.toDouble()

                            SelectedPlace.longitude =
                                place.lon.toDouble()

                            navController.navigate(
                                Screen.Journey.route
                            )

                        }
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = place.display_name,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            "Latitude : ${place.lat}"
                        )

                        Text(
                            "Longitude : ${place.lon}"
                        )

                    }

                }

            }

        }

    }

}