package com.example.parliamentvoiceapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parliamentvoiceapp.ui.components.*
import com.example.parliamentvoiceapp.ui.theme.*
import com.example.parliamentvoiceapp.viewmodel.VoiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
class VoiceScreen : ComponentActivity() {

    private val viewModel: VoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParliamentAppTheme {
                VoiceScreenContent(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceScreenContent(viewModel: VoiceViewModel, onBack: () -> Unit) {
    val micPerm = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    val isListening  by viewModel.isListening.observeAsState(false)
    val recognizedText by viewModel.correctedText.observeAsState("")
    val rmsDb        by viewModel.rmsDb.observeAsState(0f)

    LaunchedEffect(Unit) {
        if (!micPerm.status.isGranted) micPerm.launchPermissionRequest()
    }

    if (!micPerm.status.isGranted) {
        PermissionScreen { micPerm.launchPermissionRequest() }
    } else {
        VoiceSessionUI(
            isListening    = isListening,
            recognizedText = recognizedText,
            rmsDb          = rmsDb,
            onMicClicked   = {
                if (isListening) viewModel.stopListening()
                else             viewModel.startListening()
            },
            onBack = onBack
        )
    }
}

@Composable
private fun VoiceSessionUI(
    isListening: Boolean,
    recognizedText: String,
    rmsDb: Float,
    onMicClicked: () -> Unit,
    onBack: () -> Unit
) {
    // Status label targets
    val statusText = when {
        isListening && recognizedText.isEmpty() -> "Listening…"
        isListening                             -> "Keep speaking…"
        recognizedText.isNotEmpty()             -> "Transcription complete"
        else                                    -> "Tap the orb or mic to speak"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, DarkBlack, Color(0xFF040810))
                )
            )
    ) {
        // ── Background ambient glow, shifts color with state ──────────────────
        val bgGlowColor by animateColorAsState(
            targetValue = if (isListening) SaffronOrange.copy(alpha = 0.06f) else MicIdleBlue.copy(alpha = 0.04f),
            animationSpec = tween(800), label = "bg_glow"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.radialGradient(listOf(bgGlowColor, Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            TopBar(title = "Voice Mode", onBackClick = onBack)

            Spacer(Modifier.height(16.dp))

            // ── Transcription card (slides in when there's text) ──────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .heightIn(min = 0.dp, max = 160.dp)
            ) {
                ResponseCard(
                    title   = if (isListening) "Partial Result" else "Transcription",
                    content = recognizedText.ifEmpty {
                        if (isListening) "…" else "Speak now and your words will appear here in real time"
                    },
                    visible = true
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Central VoiceOrb ──────────────────────────────────────────────
            VoiceOrb(
                isListening = isListening,
                rmsDb       = rmsDb,
                modifier    = Modifier
                    .size(280.dp)
                    .alpha(1f)
            )

            Spacer(Modifier.weight(0.8f))

            // ── Wave animation strip ──────────────────────────────────────────
            WaveAnimation(
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp),
                isActive  = isListening,
                waveColor = if (isListening) SaffronOrange else MicIdleBlue,
                amplitude = 28f
            )

            Spacer(Modifier.height(12.dp))

            // ── Status label ──────────────────────────────────────────────────
            Crossfade(targetState = statusText, animationSpec = tween(400), label = "status") { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (isListening) TextAccent else TextMuted,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Mic button ────────────────────────────────────────────────────
            MicButton(
                isListening = isListening,
                onClick     = onMicClicked,
                size        = 72.dp
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PermissionScreen(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "🎙️", fontSize = 52.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Microphone Access Required",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Parliament Voice needs the microphone to transcribe your speech in real time.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                shape = RoundedCornerShape(50)
            ) {
                Text("Grant Permission", fontWeight = FontWeight.Bold)
            }
        }
    }
}