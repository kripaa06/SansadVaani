package com.example.parliamentvoiceapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.parliamentvoiceapp.ui.theme.ParliamentAppTheme

class ResultScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParliamentAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Text(text = "Speech Recognition Result Goes Here", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}