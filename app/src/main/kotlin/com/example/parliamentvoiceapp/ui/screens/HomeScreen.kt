package com.example.parliamentvoiceapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parliamentvoiceapp.ui.components.MicButton
import com.example.parliamentvoiceapp.ui.components.ResponseCard
import com.example.parliamentvoiceapp.ui.components.TopBar
import com.example.parliamentvoiceapp.ui.components.WaveAnimation
import com.example.parliamentvoiceapp.ui.theme.ParliamentAppTheme

class HomeScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParliamentAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HomeScreenContent(
                        onStartVoiceInput = {
                            startActivity(Intent(this@HomeScreen, VoiceScreen::class.java))
                        },
                        onMicClick = {
                            // TODO: Implement Mic Button functionality
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    onStartVoiceInput: () -> Unit,
    onMicClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(title = "Parliament Voice App")

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Parliament Voice App",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onStartVoiceInput) {
                Text(text = "Start Voice Input")
            }

            Spacer(modifier = Modifier.height(40.dp))

            MicButton(onClick = onMicClick)

            Spacer(modifier = Modifier.height(24.dp))

            WaveAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                waveColor = MaterialTheme.colorScheme.primary,
                amplitude = 30f
            )

            Spacer(modifier = Modifier.height(24.dp))

            ResponseCard(
                title = "Recognition Result",
                content = "This is where recognized speech text will show."
            )
        }
    }
}