package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RewivoDarkColorScheme = darkColorScheme(
    primary = AiPurple,
    onPrimary = Color.White,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = AiPurpleGlow,
    secondary = AiCyan,
    onSecondary = Color.Black,
    tertiary = AiBlue,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun RewivoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RewivoDarkColorScheme,
        typography = Typography,
        content = content
    )
}

