package com.example.parliamentvoiceapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parliamentvoiceapp.ui.components.MicButton
import com.example.parliamentvoiceapp.ui.components.ResponseCard
import com.example.parliamentvoiceapp.ui.components.TopBar
import com.example.parliamentvoiceapp.ui.theme.ParliamentAppTheme
import com.example.parliamentvoiceapp.viewmodel.VoiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalPermissionsApi::class)
class VoiceScreen : ComponentActivity() {

    private val viewModel: VoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParliamentAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    VoiceScreenContent(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceScreenContent(viewModel: VoiceViewModel) {

    val microphonePermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    val isListening by viewModel.isListening.observeAsState(false)
    val recognizedText by viewModel.correctedText.observeAsState("")

    LaunchedEffect(Unit) {
        if (!microphonePermissionState.status.isGranted) {
            microphonePermissionState.launchPermissionRequest()
        }
    }

    if (!microphonePermissionState.status.isGranted) {
        PermissionDeniedContent(onRequestPermission = {
            microphonePermissionState.launchPermissionRequest()
        })
    } else {
        VoiceRecognitionUI(
            isListening = isListening,
            recognizedText = recognizedText,
            onMicClicked = {
                if (isListening) {
                    viewModel.stopListening()
                } else {
                    viewModel.startListening()
                }
            }
        )
    }
}

@Composable
fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Microphone permission is required to use voice features.", color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRequestPermission) {
            Text(text = "Grant Permission")
        }
    }
}

@Composable
fun VoiceRecognitionUI(
    isListening: Boolean,
    recognizedText: String,
    onMicClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Voice Input")

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResponseCard(
                title = "Recognized Text",
                content = recognizedText.ifEmpty { if (isListening) "Listening..." else "Tap mic to speak" }
            )

            Spacer(modifier = Modifier.height(60.dp))

            // WaveAnimation removed as requested

            MicButton(onClick = onMicClicked)
            
            if (isListening) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Listening...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}