package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CleanMinimalismColorScheme = darkColorScheme(
    primary = MinimalPrimary,
    onPrimary = MinimalOnPrimary,
    primaryContainer = MinimalPrimaryContainer,
    onPrimaryContainer = MinimalOnPrimaryContainer,
    secondary = MinimalSecondary,
    onSecondary = MinimalOnSecondary,
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = MinimalSecondary,
    tertiary = MinimalTertiary,
    onTertiary = MinimalOnTertiary,
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = MinimalTertiary,
    background = MinimalDarkBackground,
    onBackground = MinimalOnBackground,
    surface = MinimalDarkSurface,
    onSurface = MinimalOnSurface,
    surfaceVariant = MinimalDarkSurfaceVariant,
    onSurfaceVariant = MinimalOnSurfaceVariant,
    outline = MinimalDarkOutline,
    outlineVariant = MinimalDarkOutlineVariant,
    error = MinimalError,
    onError = Color(0xFF601410)
)

private val CleanMinimalismLightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = MinimalPrimaryContainer,
    onPrimaryContainer = MinimalOnPrimaryContainer,
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    background = Color(0xFFFEF7FF),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Clean Minimalism default dark canvas
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CleanMinimalismColorScheme else CleanMinimalismLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
