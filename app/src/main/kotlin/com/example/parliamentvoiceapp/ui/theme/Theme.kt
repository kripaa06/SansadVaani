package com.example.parliamentvoiceapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
fun ParliamentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}