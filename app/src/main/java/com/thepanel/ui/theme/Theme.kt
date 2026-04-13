package com.thepanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PanelColorScheme = darkColorScheme(
    primary = AccentSky,
    secondary = AccentMint,
    tertiary = AccentSun,
    background = BackgroundStart,
    surface = SurfacePrimary,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = BackgroundStart,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0061A4),
    secondary = Color(0xFF006A60),
    tertiary = Color(0xFF7D5800),
    background = Color(0xFFF8FDFF),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E)
)

@Composable
fun ThePanelTheme(
    useLightTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useLightTheme) LightColorScheme else PanelColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
