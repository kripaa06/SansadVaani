package com.example.parliamentvoiceapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun WaveAnimation(modifier: Modifier = Modifier, waveColor: Color, amplitude: Float = 20f) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveShift = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing)
        )
    )

    Canvas(modifier = modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height / 2

        val waveLength = 400f // wave length in pixels

        var startX = -waveLength + waveShift.value

        while (startX < width) {
            // sine wave points approximated by quadratic bezier curve

            drawLine(
                color = waveColor,
                start = Offset(startX, height),
                end = Offset(startX + waveLength / 2, height + amplitude),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = waveColor,
                start = Offset(startX + waveLength / 2, height + amplitude),
                end = Offset(startX + waveLength, height),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            startX += waveLength
        }
    }
}