package com.sivasurya.autowake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sivasurya.autowake.helpers.NotificationHelper
import com.sivasurya.autowake.navigation.AppNavigation
import com.sivasurya.autowake.ui.theme.AUTOWAKETheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            AUTOWAKETheme {
                AppNavigation()
            }
        }
    }
}