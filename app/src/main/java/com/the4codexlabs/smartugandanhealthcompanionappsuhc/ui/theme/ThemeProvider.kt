package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

object ThemeProvider {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
    
    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
    }
}

@Composable
fun isAppInDarkTheme(): Boolean {
    val themeMode by ThemeProvider.themeMode.collectAsState()
    
    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}

// Enhanced color palette
val md_theme_light_primary = Color(0xFF186EDD)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD7E3FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001946)
val md_theme_light_secondary = Color(0xFF00A36C)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFB3F1D8)
val md_theme_light_onSecondaryContainer = Color(0xFF00382A)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFF8FDFF)
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF001F25)
val md_theme_light_surfaceVariant = Color(0xFFE1E2EC)
val md_theme_light_onSurfaceVariant = Color(0xFF44474F)

val md_theme_dark_primary = Color(0xFFADC7FF)
val md_theme_dark_onPrimary = Color(0xFF002D6F)
val md_theme_dark_primaryContainer = Color(0xFF00429A)
val md_theme_dark_onPrimaryContainer = Color(0xFFD7E3FF)
val md_theme_dark_secondary = Color(0xFF7ADABD)
val md_theme_dark_onSecondary = Color(0xFF00573F)
val md_theme_dark_secondaryContainer = Color(0xFF007055)
val md_theme_dark_onSecondaryContainer = Color(0xFFB3F1D8)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF001F25)
val md_theme_dark_onBackground = Color(0xFFA6EEFF)
val md_theme_dark_surface = Color(0xFF001F25)
val md_theme_dark_onSurface = Color(0xFFA6EEFF)
val md_theme_dark_surfaceVariant = Color(0xFF44474F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC5C6D0) 