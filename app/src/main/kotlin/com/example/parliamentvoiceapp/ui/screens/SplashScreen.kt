package com.example.parliamentvoiceapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.parliamentvoiceapp.ui.theme.ParliamentAppTheme
import kotlinx.coroutines.delay

class SplashScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParliamentAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Text(text = "Parliament Voice App\nLoading...", style = MaterialTheme.typography.headlineMedium)

                    // Navigate to HomeScreen after 2 seconds
                    LaunchedEffect(Unit) {
                        delay(2000)
                        startActivity(Intent(this@SplashScreen, HomeScreen::class.java))
                        finish()
                    }
                }
            }
        }
    }
}