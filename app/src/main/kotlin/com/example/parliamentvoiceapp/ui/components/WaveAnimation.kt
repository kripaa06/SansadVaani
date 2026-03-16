package com.example.parliamentvoiceapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.parliamentvoiceapp.ui.theme.SaffronOrange
import com.example.parliamentvoiceapp.ui.theme.GoldYellow
import com.example.parliamentvoiceapp.ui.theme.MicIdleBlue
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    waveColor: Color = SaffronOrange,
    amplitude: Float = 40f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Layer 1 — primary wave (fastest)
    val shift1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2000f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "w1"
    )
    // Layer 2 — secondary wave (medium)
    val shift2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2000f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)), label = "w2"
    )
    // Layer 3 — background wave (slowest)
    val shift3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2000f,
        animationSpec = infiniteRepeatable(tween(3400, easing = LinearEasing)), label = "w3"
    )

    // Amplitude ramps between 0 (flat line) and full amplitude
    val currentAmplitude by animateFloatAsState(
        targetValue = if (isActive) amplitude else 2f,
        animationSpec = tween(700, easing = EaseInOutCubic), label = "amp"
    )

    Canvas(modifier = modifier.fillMaxWidth()) {
        val w = size.width
        val cy = size.height / 2f
        val step = 4f

        fun buildPath(shift: Float, ampMul: Float): Path {
            val p = Path()
            var x = 0f
            var first = true
            val wl = 460f
            while (x <= w + step) {
                val y = cy + currentAmplitude * ampMul * sin(2.0 * PI * (x - shift) / wl).toFloat()
                if (first) { p.moveTo(x, y); first = false } else p.lineTo(x, y)
                x += step
            }
            return p
        }

        // Layer 3 — faintest, widest
        drawPath(
            path = buildPath(shift3, 0.55f),
            color = waveColor.copy(alpha = if (isActive) 0.18f else 0.08f),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
        // Layer 2 — mid
        drawPath(
            path = buildPath(shift2, 0.80f),
            color = waveColor.copy(alpha = if (isActive) 0.35f else 0.12f),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
        // Layer 1 — brightest, narrowest
        drawPath(
            path = buildPath(shift1, 1.0f),
            color = waveColor.copy(alpha = if (isActive) 0.75f else 0.20f),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        // Center flat guide line (when inactive a clean baseline shows)
        if (!isActive) {
            drawLine(
                color = waveColor.copy(alpha = 0.15f),
                start = Offset(0f, cy),
                end = Offset(w, cy),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
    }
}