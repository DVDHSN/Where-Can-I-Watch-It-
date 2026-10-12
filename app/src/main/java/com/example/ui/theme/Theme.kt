package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CinemaPrimary,
    onPrimary = CinemaOnPrimary,
    primaryContainer = CinemaPrimaryContainer,
    onPrimaryContainer = CinemaOnPrimaryContainer,
    secondary = CinemaSecondary,
    onSecondary = Color.Black,
    background = CinemaBackground,
    onBackground = CinemaTextPrimary,
    surface = CinemaSurface,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = CinemaTextSecondary,
    outline = CinemaBorder,
    outlineVariant = CinemaBorderLight
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFCA8A04),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF08A),
    onPrimaryContainer = Color(0xFF713F12),
    secondary = Color(0xFFEAB308),
    onSecondary = Color.Black,
    background = Color(0xFFFAF9F6),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF52525B),
    outline = Color(0xFFE4E4E7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Default to dark theme for cinema experience
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
