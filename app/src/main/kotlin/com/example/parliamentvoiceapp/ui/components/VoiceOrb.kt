package com.example.parliamentvoiceapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.parliamentvoiceapp.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 *  VoiceOrb — ChatGPT-style animated voice orb.
 *
 *  Idle  → slowly breathes, blue glow
 *  Active → multi-ring pulse, amplitude-driven scale, saffron/orange glow
 *  rmsDb → normalised voice amplitude (0..10+ dB from Android speech API)
 */
@Composable
fun VoiceOrb(
    isListening: Boolean,
    rmsDb: Float = 0f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_inf")

    // ── Idle breathing ────────────────────────────────────────────────────────
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.93f, targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOut), RepeatMode.Reverse),
        label = "breath"
    )

    // ── Slow rotation for outer decorative arc segments ───────────────────────
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "rot"
    )
    val rotationReverse by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing)),
        label = "rot_rev"
    )

    // ── Expanding ripple rings (listening) ────────────────────────────────────
    val ripple1 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseOut), RepeatMode.Restart),
        label = "rpl1"
    )
    val ripple1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseOut), RepeatMode.Restart),
        label = "rpl1a"
    )
    val ripple2 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            tween(1400, delayMillis = 450, easing = EaseOut), RepeatMode.Restart),
        label = "rpl2"
    )
    val ripple2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(1400, delayMillis = 450, easing = EaseOut), RepeatMode.Restart),
        label = "rpl2a"
    )
    val ripple3 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            tween(1400, delayMillis = 900, easing = EaseOut), RepeatMode.Restart),
        label = "rpl3"
    )
    val ripple3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(1400, delayMillis = 900, easing = EaseOut), RepeatMode.Restart),
        label = "rpl3a"
    )

    // ── Amplitude-driven scale (voice loudness) ───────────────────────────────
    val normalizedAmp = ((rmsDb / 10f).coerceIn(0f, 1f))
    val voiceScale by animateFloatAsState(
        targetValue = if (isListening) 1f + normalizedAmp * 0.28f else breathScale,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
        label = "voice_scale"
    )

    // ── Color transitions ─────────────────────────────────────────────────────
    val coreColor by animateColorAsState(
        targetValue = if (isListening) SaffronOrange else OrbIdleCore,
        animationSpec = tween(700), label = "core_col"
    )
    val midColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFFFF4D00) else OrbMid,
        animationSpec = tween(700), label = "mid_col"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isListening) 0.20f else 0.10f,
        animationSpec = tween(600), label = "glow_a"
    )

    Canvas(modifier = modifier) {
        val center = this.center
        // Base orb radius = 38% of the shortest canvas dimension
        val baseR = size.minDimension * 0.38f
        val orbR  = baseR * voiceScale

        // ── Outer ambient glow ────────────────────────────────────────────────
        drawCircle(color = coreColor.copy(alpha = glowAlpha * 0.5f), radius = orbR * 2.0f, center = center)
        drawCircle(color = coreColor.copy(alpha = glowAlpha),        radius = orbR * 1.55f, center = center)

        // ── Ripple rings (listening) ───────────────────────────────────────────
        if (isListening) {
            drawCircle(
                color = coreColor.copy(alpha = ripple1Alpha * 0.55f),
                radius = orbR * ripple1, center = center,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(
                color = coreColor.copy(alpha = ripple2Alpha * 0.45f),
                radius = orbR * ripple2, center = center,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(
                color = coreColor.copy(alpha = ripple3Alpha * 0.35f),
                radius = orbR * ripple3, center = center,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // ── Rotating outer arc orbit ring ─────────────────────────────────────
        val orbitR = orbR * 1.35f
        val segCount = 6
        val segArc   = 28f   // degrees per segment
        val segGap   = (360f - segCount * segArc) / segCount
        for (i in 0 until segCount) {
            val startAngle = rotation + i * (segArc + segGap)
            drawArc(
                color = coreColor.copy(alpha = if (isListening) 0.40f else 0.18f),
                startAngle = startAngle,
                sweepAngle = segArc,
                useCenter = false,
                topLeft = Offset(center.x - orbitR, center.y - orbitR),
                size = androidx.compose.ui.geometry.Size(orbitR * 2, orbitR * 2),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Reverse slower orbit ring
        val orbitR2 = orbR * 1.6f
        for (i in 0 until 4) {
            val startAngle = rotationReverse + i * (45f + 45f)
            drawArc(
                color = midColor.copy(alpha = if (isListening) 0.22f else 0.10f),
                startAngle = startAngle,
                sweepAngle = 35f,
                useCenter = false,
                topLeft = Offset(center.x - orbitR2, center.y - orbitR2),
                size = androidx.compose.ui.geometry.Size(orbitR2 * 2, orbitR2 * 2),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // ── Gradient core (simulated with concentric circles) ─────────────────
        // Layer 4 — outermost, faintest
        drawCircle(color = midColor.copy(alpha  = 0.35f), radius = orbR * 0.98f, center = center)
        // Layer 3
        drawCircle(color = coreColor.copy(alpha = 0.60f), radius = orbR * 0.80f, center = center)
        // Layer 2 — inner bright
        drawCircle(color = coreColor.copy(alpha = 0.85f), radius = orbR * 0.55f, center = center)
        // Layer 1 — hot core
        drawCircle(color = Color.White.copy(alpha  = 0.18f), radius = orbR * 0.28f, center = center)

        // ── Specular highlight (top-left) ─────────────────────────────────────
        val hlOffset = Offset(center.x - orbR * 0.24f, center.y - orbR * 0.30f)
        drawCircle(color = Color.White.copy(alpha = 0.22f), radius = orbR * 0.18f, center = hlOffset)

        // ── Amplitude dot ring (8 dots pulsing when listening) ─────────────────
        if (isListening && normalizedAmp > 0.05f) {
            val dotR  = orbR * 1.15f
            val dotCount = 12
            for (i in 0 until dotCount) {
                val angle = (i.toDouble() * 2.0 * PI / dotCount).toFloat()
                val dx = cos(angle) * dotR
                val dy = sin(angle) * dotR
                val dotAlpha = (0.3f + normalizedAmp * 0.7f) *
                    (0.5f + 0.5f * sin((angle + rotation * PI.toFloat() / 180f).toDouble()).toFloat())
                drawCircle(
                    color = coreColor.copy(alpha = dotAlpha.coerceIn(0.05f, 0.9f)),
                    radius = (2.5f + normalizedAmp * 4f).dp.toPx(),
                    center = Offset(center.x + dx, center.y + dy)
                )
            }
        }
    }
}
