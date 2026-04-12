package com.thepanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun ThePanelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PanelColorScheme,
        content = content
    )
}
