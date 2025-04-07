package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * Custom Application class for the Smart Ugandan Health Companion App.
 * Handles initialization of Firebase, WorkManager, and notification channels.
 */
class SUHCApplication : Application(), Configuration.Provider {
    
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics
        
        // Create notification channels
        createNotificationChannels()
        
        // Initialize WorkManager
        WorkManager.initialize(this, workManagerConfiguration)
    }
    
    /**
     * Creates notification channels for different types of notifications.
     * Only needed for Android O (API 26) and above.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Health reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Health Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders for health check-ins and medication"
                enableVibration(true)
            }
            
            // SOS alerts channel
            val sosChannel = NotificationChannel(
                CHANNEL_SOS,
                "SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency SOS alerts"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // General notifications channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }
            
            notificationManager.createNotificationChannels(
                listOf(remindersChannel, sosChannel, generalChannel)
            )
        }
    }
    
    /**
     * Provides WorkManager configuration.
     * This is needed to initialize WorkManager properly with our custom Application class.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
            
    // WorkManager configuration is provided via the property above
    
    companion object {
        // Notification channel IDs
        const val CHANNEL_REMINDERS = "reminders_channel"
        const val CHANNEL_SOS = "sos_channel"
        const val CHANNEL_GENERAL = "general_channel"
    }
}