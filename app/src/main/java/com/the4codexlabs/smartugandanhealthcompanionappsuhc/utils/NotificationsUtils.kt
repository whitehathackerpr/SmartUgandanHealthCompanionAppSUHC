package com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Utility class for managing notification preferences.
 * Handles notification settings using DataStore.
 */
class NotificationsUtils(private val context: Context) {

    /**
     * Get the current notification status.
     * @return Flow of the current notification status (enabled or disabled).
     */
    fun getNotificationsEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] ?: DEFAULT_NOTIFICATIONS_ENABLED
        }
    }

    /**
     * Set the notification status.
     * @param enabled Whether notifications should be enabled.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    companion object {
        // Default value
        const val DEFAULT_NOTIFICATIONS_ENABLED = true
        
        // DataStore setup
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notifications_preferences")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    }
}