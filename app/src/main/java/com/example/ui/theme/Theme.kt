package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClayColorScheme = lightColorScheme(
    primary = ClayViolet,
    secondary = ClayMint,
    tertiary = ClayPink,
    background = ClayBackground,
    surface = ClayCardBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = ClayTextCharcoal,
    onSurface = ClayTextCharcoal
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ClayColorScheme,
        typography = Typography,
        content = content
    )
}
