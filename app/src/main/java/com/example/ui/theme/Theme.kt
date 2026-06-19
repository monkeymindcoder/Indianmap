package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CartoPrimary,
    onPrimary = Color.White,
    secondary = CartoSecondary,
    onSecondary = CartoTextDark,
    background = CartoBackground,
    onBackground = CartoTextDark,
    surface = CartoSurface,
    onSurface = CartoTextDark,
    outline = CartoTextDark,
    surfaceVariant = Color(0xFFF1EBC2),
    onSurfaceVariant = CartoTextDark
)

// A dark slate-tint parchment for those who might prefer a "night-cartography" mode,
// but still strictly keeping light-yellow elements and dark contrast.
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4AF37), // Brighter historical gold
    onPrimary = Color.Black,
    secondary = CartoSecondary,
    onSecondary = Color.Black,
    background = Color(0xFF28251B), // Dark, warm charcoal yellow
    onBackground = Color(0xFFFAF7E6), // Creamy light text
    surface = Color(0xFF383424), // Medium warm charcoal
    onSurface = Color(0xFFFAF7E6),
    outline = Color(0xFFFAF7E6)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep it light-parchment by default for the physical-map look!
    dynamicColor: Boolean = false, // Force our custom map color palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
