package com.example.parliamentvoiceapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parliamentvoiceapp.ui.components.MicButton
import com.example.parliamentvoiceapp.ui.components.TopBar
import com.example.parliamentvoiceapp.ui.components.VoiceOrb
import com.example.parliamentvoiceapp.ui.theme.*

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParliamentAppTheme {
                HomeScreenContent(
                    onStartVoice = {
                        startActivity(Intent(this@HomeScreen, VoiceScreen::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(onStartVoice: () -> Unit) {
    var show by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(if (show) 1f else 0f, tween(700), label = "ha")
    LaunchedEffect(Unit) { show = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, DarkBlack, DarkBlack)
                )
            )
    ) {
        // ── Background decorative circles ─────────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = 80.dp)
                .size(260.dp)
                .alpha(0.08f)
                .background(
                    brush = Brush.radialGradient(listOf(SaffronOrange, Color.Transparent)),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .size(200.dp)
                .alpha(0.07f)
                .background(
                    brush = Brush.radialGradient(listOf(MicIdleBlue, Color.Transparent)),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(title = "Parliament Voice")

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .alpha(contentAlpha)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Hero tagline ──────────────────────────────────────────────
                Text(
                    text = "Your Voice,",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Parliament's Record.",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "AI-powered voice transcription for Parliament proceedings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))

                // ── Preview VoiceOrb (idle, clickable) ────────────────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(240.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onStartVoice
                        )
                ) {
                    VoiceOrb(isListening = false, rmsDb = 0f, modifier = Modifier.fillMaxSize())
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TAP TO SPEAK", style = MaterialTheme.typography.labelSmall, color = TextMuted, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.height(36.dp))

                // ── Feature cards row ─────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(emoji = "🎙️", label = "Live Transcription", modifier = Modifier.weight(1f))
                    FeatureCard(emoji = "✏️", label = "Auto Correction",   modifier = Modifier.weight(1f))
                    FeatureCard(emoji = "🇮🇳", label = "Indian English",    modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(32.dp))

                // ── Primary CTA button ────────────────────────────────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.horizontalGradient(listOf(SaffronOrange, GoldYellow))
                        )
                        .clickable(onClick = onStartVoice)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Start Voice Session",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(emoji: String, label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .background(GlassSurface)
            .border(1.dp, GlassBorder, shape)
            .padding(vertical = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}