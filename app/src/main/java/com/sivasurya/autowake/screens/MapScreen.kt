package com.sivasurya.autowake.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.sivasurya.autowake.navigation.Screen
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapScreen(navController: NavController) {

    val context = LocalContext.current

    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", 0)
        )

        onDispose { }
    }

    lateinit var mapView: MapView

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(

            modifier = Modifier.fillMaxSize(),

            factory = {

                mapView = MapView(context).apply {

                    setMultiTouchControls(true)

                    zoomController.setVisibility(
                        CustomZoomButtonsController.Visibility.ALWAYS
                    )

                    controller.setZoom(15.0)

                    controller.setCenter(
                        GeoPoint(
                            11.2754,
                            77.6070
                        )
                    )

                }

                mapView
            }

        )

        // Crosshair
        Text(
            text = "✚",
            modifier = Modifier.align(Alignment.Center)
        )

        Button(

            onClick = {

                val point = mapView.mapCenter as GeoPoint

                navController.navigate(
                    Screen.Journey.createRoute(
                        point.latitude,
                        point.longitude
                    )
                )

            },

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)

        ) {

            Text("Select This Location")

        }

    }

}