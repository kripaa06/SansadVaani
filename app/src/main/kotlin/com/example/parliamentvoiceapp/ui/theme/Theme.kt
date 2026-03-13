package com.example.parliamentvoiceapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondarySaffron,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

private val LightColors = lightColorScheme(
    primary = DeepBlue,
    secondary = Saffron,
    background = BackgroundGray,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DeepBlue,
    onSurface = DeepBlue,
)

@Composable
fun ParliamentAppTheme(
    darkTheme: Boolean = true, // Default to dark theme
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}