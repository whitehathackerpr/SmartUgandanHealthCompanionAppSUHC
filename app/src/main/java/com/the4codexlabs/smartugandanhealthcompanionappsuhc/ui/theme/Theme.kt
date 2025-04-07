package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepGreen,
    secondary = SunGold,
    tertiary = GreenGradientEnd,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkText,
    onSecondary = DarkBackground,
    onTertiary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkText.copy(alpha = 0.8f),
    outline = DarkOutline,
    error = SOSRed,
    onError = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = DeepGreen,
    secondary = SunGold,
    tertiary = GreenGradientEnd,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = LightBackground,
    onSecondary = LightText,
    onTertiary = LightBackground,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightText.copy(alpha = 0.8f),
    outline = LightOutline,
    error = SOSRed,
    onError = LightBackground
)

@Composable
fun SmartUgandanHealthCompanionAppSUHCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
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
        shapes = AppShapes,
        content = content
    )
}