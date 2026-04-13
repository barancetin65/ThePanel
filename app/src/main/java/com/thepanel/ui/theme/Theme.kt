package com.thepanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentSky,
    secondary = AccentMint,
    tertiary = AccentSun,
    background = BackgroundStart,
    surface = SurfacePrimary,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = BackgroundStart,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextMuted,
    surfaceVariant = SurfaceSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentSky,
    secondary = AccentMint,
    tertiary = AccentSun,
    background = LightBackgroundStart,
    surface = LightSurfacePrimary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = LightBackgroundStart,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextMuted,
    surfaceVariant = LightSurfaceSecondary
)

@Composable
fun ThePanelTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
