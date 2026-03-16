package com.example.parliamentvoiceapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary          = SaffronOrange,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF5A1F00),
    secondary        = GoldYellow,
    onSecondary      = Color(0xFF1A1000),
    tertiary         = MicIdleBlue,
    onTertiary       = Color.White,
    background       = DarkBlack,
    onBackground     = TextPrimary,
    surface          = CardSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = NavyBlue,
    onSurfaceVariant = TextSecondary,
    outline          = GlassBorder,
    error            = Color(0xFFCF6679),
    onError          = Color.White,
)

private val AppShapes = Shapes(
    small  = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large  = RoundedCornerShape(24.dp),
)

@Composable
fun ParliamentAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}