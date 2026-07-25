package com.sivasurya.autowake.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sivasurya.autowake.model.Place
import com.sivasurya.autowake.model.SelectedPlace
import com.sivasurya.autowake.navigation.Screen
import com.sivasurya.autowake.repository.SearchRepository
import kotlinx.coroutines.launch

@Composable
fun DestinationScreen(navController: NavController) {

    var searchText by remember { mutableStateOf("") }
    var places by remember { mutableStateOf<List<Place>>(emptyList()) }

    val repository = remember { SearchRepository() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Choose Destination",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Search a place or select it from the map.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },
            label = {
                Text("Search destination")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                scope.launch {

                    try {
                        places = repository.search(searchText)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Icon(
                Icons.Default.Search,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Search")

        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {

                // KEEP ORIGINAL FLOW
                navController.navigate(Screen.Map.route)

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Icon(
                Icons.Default.Map,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Select Destination From Map")

        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(places) { place ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            SelectedPlace.name = place.display_name
                            SelectedPlace.latitude = place.lat.toDouble()
                            SelectedPlace.longitude = place.lon.toDouble()

                            // FIXED
                            navController.navigate(
                                Screen.Journey.createRoute(
                                    place.lat.toDouble(),
                                    place.lon.toDouble()
                                )
                            )

                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = place.display_name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Latitude: ${place.lat}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Longitude: ${place.lon}",
                            style = MaterialTheme.typography.bodySmall
                        )

                    }

                }

            }

        }

    }

}