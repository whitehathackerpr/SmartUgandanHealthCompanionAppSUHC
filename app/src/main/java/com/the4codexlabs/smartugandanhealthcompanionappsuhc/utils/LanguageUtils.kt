package com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

/**
 * Utility class for managing app language settings.
 * Handles language switching and persistence using DataStore.
 */
class LanguageUtils(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    
    companion object {
        // Language codes
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_LUGANDA = "lg"
        const val LANGUAGE_RUNYANKOLE = "nyn"
        
        // Default language
        const val DEFAULT_LANGUAGE = LANGUAGE_ENGLISH
        
        // DataStore key
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
        
        /**
         * Get the display name for a language code.
         */
        fun getLanguageDisplayName(languageCode: String, context: Context): String {
            return when (languageCode) {
                LANGUAGE_ENGLISH -> context.getString(com.the4codexlabs.smartugandanhealthcompanionappsuhc.R.string.english)
                LANGUAGE_LUGANDA -> context.getString(com.the4codexlabs.smartugandanhealthcompanionappsuhc.R.string.luganda)
                LANGUAGE_RUNYANKOLE -> context.getString(com.the4codexlabs.smartugandanhealthcompanionappsuhc.R.string.runyankole)
                else -> context.getString(com.the4codexlabs.smartugandanhealthcompanionappsuhc.R.string.english)
            }
        }
        
        /**
         * Get all supported languages.
         */
        fun getSupportedLanguages(): List<String> {
            return listOf(LANGUAGE_ENGLISH, LANGUAGE_LUGANDA, LANGUAGE_RUNYANKOLE)
        }
    }
    
    /**
     * Get the current app language.
     * @return Flow of the current language code.
     */
    fun getAppLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: getSystemLanguage()
        }
    }
    
    /**
     * Set the app language.
     * @param languageCode The language code to set.
     */
    suspend fun setAppLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
        applyLanguage(languageCode)
    }
    
    /**
     * Apply the selected language to the app.
     * @param languageCode The language code to apply.
     */
    fun applyLanguage(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    
    /**
     * Get the system language, or default to English if not supported.
     * @return The system language code if supported, or DEFAULT_LANGUAGE.
     */
    private fun getSystemLanguage(): String {
        val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].language
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale.language
        }
        
        return if (getSupportedLanguages().contains(systemLanguage)) {
            systemLanguage
        } else {
            DEFAULT_LANGUAGE
        }
    }
    
    /**
     * Create a configuration with the specified language.
     * Used for legacy language switching.
     * @param languageCode The language code to use.
     * @return A Configuration object with the specified locale.
     */
    @Suppress("DEPRECATION")
    fun createConfigurationContext(languageCode: String): Configuration {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        
        return config
    }
}