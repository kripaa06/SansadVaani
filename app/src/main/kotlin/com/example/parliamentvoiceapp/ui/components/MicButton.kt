package com.example.parliamentvoiceapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.parliamentvoiceapp.ui.theme.*

@Composable
fun MicButton(
    isListening: Boolean = false,
    onClick: () -> Unit,
    size: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")

    // Concentric pulse rings ─────────────────────────────────────────────────
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r1s"
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.75f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r1a"
    )
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            tween(1600, delayMillis = 500, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r2s"
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(1600, delayMillis = 500, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r2a"
    )
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            tween(1600, delayMillis = 1000, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r3s"
    )
    val ring3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.45f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(1600, delayMillis = 1000, easing = FastOutSlowInEasing), RepeatMode.Restart
        ), label = "r3a"
    )

    // Idle gentle breathing ──────────────────────────────────────────────────
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = EaseInOut), RepeatMode.Reverse
        ), label = "breath"
    )

    // State-driven animations ────────────────────────────────────────────────
    val buttonScale by animateFloatAsState(
        targetValue = if (isListening) 1.08f else breathScale,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "btn_scale"
    )
    val buttonColor by animateColorAsState(
        targetValue = if (isListening) MicActiveOrange else MicIdleBlue,
        animationSpec = tween(500), label = "btn_color"
    )
    val glowColor by animateColorAsState(
        targetValue = if (isListening) MicGlowOrange else MicGlowBlue,
        animationSpec = tween(500), label = "glow"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size * 3f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // ── Pulse rings (listening only) ────────────────────────────────────
        Canvas(modifier = Modifier.matchParentSize()) {
            val r = size.toPx() / 2f
            if (isListening) {
                drawCircle(
                    color = buttonColor.copy(alpha = ring1Alpha * 0.55f),
                    radius = r * ring1Scale, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(
                    color = buttonColor.copy(alpha = ring2Alpha * 0.4f),
                    radius = r * ring2Scale, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(
                    color = buttonColor.copy(alpha = ring3Alpha * 0.3f),
                    radius = r * ring3Scale, style = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            // Glow halo (always visible)
            drawCircle(color = glowColor, radius = r * buttonScale * 1.35f)
            // Core button circle
            drawCircle(color = buttonColor, radius = r * buttonScale)
        }

        // ── Mic / Stop icon ─────────────────────────────────────────────────
        Icon(
            imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = if (isListening) "Stop" else "Start Recording",
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier
                .size(size * 0.44f)
                .scale(buttonScale)
        )
    }
}