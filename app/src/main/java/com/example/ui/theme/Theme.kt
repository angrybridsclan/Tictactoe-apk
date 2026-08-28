package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = NeonDarkSurface,
    onPrimaryContainer = NeonCyanLight,
    secondary = NeonCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2A0D1B),
    onSecondaryContainer = NeonCoralLight,
    tertiary = NeonGold,
    onTertiary = Color.Black,
    background = NeonDarkBg,
    onBackground = Color.White,
    surface = NeonDarkSurface,
    onSurface = Color.White,
    surfaceVariant = NeonCardBg,
    onSurfaceVariant = NeonTextMuted,
    outline = NeonPurpleBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeonColorScheme,
        typography = Typography,
        content = content
    )
}
