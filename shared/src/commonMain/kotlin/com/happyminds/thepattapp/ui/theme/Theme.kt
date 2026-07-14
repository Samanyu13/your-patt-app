package com.happyminds.thepattapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MayaBlue,
    onPrimary = Color.Black,
    primaryContainer = UranianBlue,
    onPrimaryContainer = Color.Black,
    secondary = Pink,
    onSecondary = Color.Black,
    secondaryContainer = CarnationPink,
    onSecondaryContainer = Color.Black,
    tertiary = Lavender,
    onTertiary = Color.Black,
    tertiaryContainer = Lavender,
    onTertiaryContainer = Color.Black,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = UranianBlue,
    onSurfaceVariant = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = MayaBlue,
    onPrimary = Color.Black,
    primaryContainer = UranianBlue,
    onPrimaryContainer = Color.Black,
    secondary = Pink,
    onSecondary = Color.Black,
    secondaryContainer = CarnationPink,
    onSecondaryContainer = Color.Black,
    tertiary = Lavender,
    onTertiary = Color.Black,
    tertiaryContainer = Lavender,
    onTertiaryContainer = Color.Black,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onBackground = Color(0xFFFFFBFE),
    onSurface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
