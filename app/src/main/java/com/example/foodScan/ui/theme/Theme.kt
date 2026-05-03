package com.example.foodScan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppGreen = Color(0xFF2DB673)
private val AppGreenDark = Color(0xFF1E8A55)
private val AppBackground = Color(0xFFF4F6F8)
private val AppSurface = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = AppGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7F0D5),
    onPrimaryContainer = Color(0xFF00391E),
    secondary = Color(0xFF4A90D9),
    onSecondary = Color.White,
    background = AppBackground,
    onBackground = Color(0xFF1A1A1A),
    surface = AppSurface,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEEF0F3),
    onSurfaceVariant = Color(0xFF6B7280),
    error = Color(0xFFE63E11),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AppGreen,
    onPrimary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun FoodScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}