package com.sarkari.exam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = AccentPurple,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = PrimaryBlue,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    outline = BorderColor
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = Color(0xFF1E293B),
    tertiary = GeminiGradientMiddle,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = BackgroundWhite,
    onSecondary = BackgroundWhite,
    onBackground = BackgroundWhite,
    onSurface = BackgroundWhite,
    error = ErrorRed,
    outline = Color(0xFF334155)
)

@Composable
fun SarkariExamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
