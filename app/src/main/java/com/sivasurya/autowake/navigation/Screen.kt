package com.sivasurya.autowake.navigation

sealed class Screen(val route: String) {

    object Home : Screen("home")

    object Destination : Screen("destination")

    object Journey : Screen("journey/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String {
            return "journey/$lat/$lon"
        }
    }

    object Map : Screen("map")

}