package com.example.parliamentvoiceapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parliamentvoiceapp.ui.theme.*
import kotlinx.coroutines.delay

class SplashScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParliamentAppTheme {
                SplashContent {
                    startActivity(Intent(this@SplashScreen, HomeScreen::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
private fun SplashContent(onFinish: () -> Unit) {
    // ── Animation states ─────────────────────────────────────────────────────
    var show by remember { mutableStateOf(false) }
    val alpha    by animateFloatAsState(if (show) 1f else 0f, tween(900), label = "alpha")
    val scale    by animateFloatAsState(if (show) 1f else 0.78f, tween(900, easing = EaseOutBack), label = "scale")
    val tagAlpha by animateFloatAsState(if (show) 1f else 0f, tween(900, delayMillis = 350), label = "tag")

    // Orbiter glow rotation
    val infiniteTransition = rememberInfiniteTransition(label = "splash_inf")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)), label = "rot"
    )

    LaunchedEffect(Unit) {
        show = true
        delay(2600)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(NavyBlue, DeepNavy, DarkBlack),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Decorative background glow ────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .alpha(0.3f)
                .background(
                    brush = Brush.radialGradient(listOf(SaffronOrange, Color.Transparent)),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // ── Parliament Orb Logo ───────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
                    .size(130.dp)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(SaffronOrange.copy(0.25f), Color.Transparent)
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                // Core circle
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(GoldYellow, SaffronOrange, Color(0xFFCC3D00))
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏛",
                        fontSize = 36.sp
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── App Title ─────────────────────────────────────────────────────
            Text(
                text = "Parliament Voice",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimary,
                modifier = Modifier.alpha(alpha).scale(scale)
            )

            Spacer(Modifier.height(10.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text = "Speak. Transcribe. Engage.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextAccent,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(tagAlpha)
            )

            Spacer(Modifier.height(48.dp))

            // ── Loading dots ──────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(tagAlpha)
            ) {
                repeat(3) { i ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(600, delayMillis = i * 200, easing = EaseInOut),
                            RepeatMode.Reverse
                        ), label = "dot_$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .alpha(dotAlpha)
                            .background(SaffronOrange, androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        }
    }
}