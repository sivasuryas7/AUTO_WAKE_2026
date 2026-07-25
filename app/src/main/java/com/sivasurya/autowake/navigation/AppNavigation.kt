package com.sivasurya.autowake.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sivasurya.autowake.screens.DestinationScreen
import com.sivasurya.autowake.screens.HomeScreen
import com.sivasurya.autowake.screens.JourneyScreen
import com.sivasurya.autowake.screens.MapScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Destination.route) {
            DestinationScreen(navController)
        }

        composable(Screen.Map.route) {
            MapScreen(navController)
        }

        composable(
            route = Screen.Journey.route,
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                },
                navArgument("lon") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val lat = backStackEntry.arguments
                ?.getString("lat")
                ?.toDoubleOrNull() ?: 0.0

            val lon = backStackEntry.arguments
                ?.getString("lon")
                ?.toDoubleOrNull() ?: 0.0

            JourneyScreen(
                latitude = lat,
                longitude = lon
            )
        }

    }

}