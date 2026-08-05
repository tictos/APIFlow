package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SendButtonPeriwinkle,
    onPrimary = SendButtonText,
    primaryContainer = NavActiveGreenPill,
    onPrimaryContainer = NavActiveGreenText,
    secondary = NavActiveGreenPill,
    onSecondary = NavActiveGreenText,
    background = DarkCanvas,
    surface = DarkSurface,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkInputBackground,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkSurfaceBorder,
    error = MethodDeleteColor
)

private val LightColorScheme = DarkColorScheme // Default to dark theme matching API Debugger UI screenshot

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Forces screenshot-aligned dark theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


