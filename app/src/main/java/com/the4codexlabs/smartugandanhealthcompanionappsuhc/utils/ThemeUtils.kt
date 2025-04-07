package com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Utility class for managing app theme settings.
 * Handles theme preferences using DataStore and applies them to the app.
 */
class ThemeUtils(private val context: Context) {

    /**
     * Get the current app theme.
     * @return Flow of the current theme (light, dark, or system).
     */
    fun getAppTheme(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: THEME_SYSTEM
        }
    }

    /**
     * Set the app theme.
     * @param theme The theme to set (light, dark, or system).
     */
    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
        
        // Apply the theme immediately
        applyTheme(theme)
    }

    /**
     * Apply the theme to the app.
     * @param theme The theme to apply (light, dark, or system).
     */
    private fun applyTheme(theme: String) {
        val mode = when (theme) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        // Theme constants
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
        
        // DataStore setup
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
        private val THEME_KEY = stringPreferencesKey("app_theme")
    }
}