package com.the4codexlabs.smartugandanhealthcompanionappsuhc.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.MainActivity
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_SOS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Foreground service for SOS emergency location tracking.
 * This service tracks the user's location during an emergency and sends updates
 * to emergency contacts and Firestore.
 */
class SOSService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var emergencyId: String? = null
    private var userId: String? = null
    
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                    processNewLocation(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SOS -> {
                userId = intent.getStringExtra(EXTRA_USER_ID)
                if (!isTracking) {
                    startSosTracking()
                }
            }
            ACTION_STOP_SOS -> {
                stopSosTracking()
            }
        }
        
        // If service is killed, restart it
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        serviceScope.cancel()
    }

    /**
     * Start SOS tracking with a foreground notification.
     */
    private fun startSosTracking() {
        isTracking = true
        emergencyId = "emergency_${System.currentTimeMillis()}"
        
        // Create initial emergency record in Firestore
        serviceScope.launch {
            try {
                val initialLocation = getCurrentLocation()
                createEmergencyRecord(initialLocation)
                
                // Start foreground service with notification
                startForeground(NOTIFICATION_ID, createNotification())
                
                // Request location updates
                requestLocationUpdates()
                
                // Send initial SOS message to emergency contacts
                sendSosToContacts(initialLocation)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting SOS tracking", e)
            }
        }
    }

    /**
     * Stop SOS tracking and service.
     */
    private fun stopSosTracking() {
        isTracking = false
        stopLocationUpdates()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        
        // Update emergency record to mark it as resolved
        emergencyId?.let { id ->
            serviceScope.launch {
                try {
                    firestore.collection("emergencies")
                        .document(id)
                        .update("resolved", true, "resolvedAt", Date())
                } catch (e: Exception) {
                    Log.e(TAG, "Error resolving emergency", e)
                }
            }
        }
    }

    /**
     * Request location updates with high accuracy and frequency.
     */
    private fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 
                UPDATE_INTERVAL
            ).apply {
                setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                setMaxUpdateDelayMillis(MAX_WAIT_TIME)
            }.build()
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
        }
    }

    /**
     * Stop location updates.
     */
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Get the current location once.
     */
    private suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
            null
        }
    }

    /**
     * Process a new location update.
     */
    private fun processNewLocation(location: Location) {
        serviceScope.launch {
            try {
                // Update emergency location in Firestore
                updateEmergencyLocation(location)
                
                // Periodically update emergency contacts (not on every location update)
                // This is to avoid sending too many messages
                if (System.currentTimeMillis() % (UPDATE_CONTACTS_INTERVAL) < UPDATE_INTERVAL) {
                    sendLocationUpdateToContacts(location)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing location update", e)
            }
        }
    }

    /**
     * Create an emergency record in Firestore.
     */
    private suspend fun createEmergencyRecord(location: Location?) {
        emergencyId?.let { id ->
            val emergency = hashMapOf(
                "userId" to userId,
                "emergencyId" to id,
                "startedAt" to Date(),
                "resolved" to false,
                "initialLatitude" to location?.latitude,
                "initialLongitude" to location?.longitude,
                "currentLatitude" to location?.latitude,
                "currentLongitude" to location?.longitude,
                "locationUpdates" to listOf(
                    hashMapOf(
                        "timestamp" to Date(),
                        "latitude" to location?.latitude,
                        "longitude" to location?.longitude,
                        "accuracy" to location?.accuracy
                    )
                )
            )
            
            firestore.collection("emergencies")
                .document(id)
                .set(emergency)
        }
    }

    /**
     * Update emergency location in Firestore.
     */
    private suspend fun updateEmergencyLocation(location: Location) {
        emergencyId?.let { id ->
            val locationUpdate = hashMapOf(
                "timestamp" to Date(),
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "accuracy" to location.accuracy
            )
            
            firestore.collection("emergencies")
                .document(id)
                .update(
                    "currentLatitude", location.latitude,
                    "currentLongitude", location.longitude,
                    "locationUpdates", com.google.firebase.firestore.FieldValue.arrayUnion(locationUpdate)
                )
        }
    }

    /**
     * Send SOS alert to emergency contacts.
     */
    private suspend fun sendSosToContacts(location: Location?) {
        try {
            val userId = userId ?: return
            
            // Get emergency contacts from Firestore
            val contactsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("emergency_contacts")
                .get()
                .await()
                
            if (contactsSnapshot.isEmpty) {
                Log.d(TAG, "No emergency contacts found for user $userId")
                return
            }
            
            // Create emergency message with location
            val locationText = if (location != null) {
                "Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
            } else {
                "Location information not available"
            }
            
            val message = "EMERGENCY ALERT: I need help! $locationText"
            
            // Send notifications to emergency contacts
            for (document in contactsSnapshot.documents) {
                val contactName = document.getString("name") ?: continue
                val contactPhone = document.getString("phone") ?: continue
                
                // Send SMS if permission is granted
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (applicationContext.checkSelfPermission(android.Manifest.permission.SEND_SMS) == 
                            android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        try {
                            // Use SMS Manager to send text
                            android.telephony.SmsManager.getDefault().sendTextMessage(
                                contactPhone,
                                null,
                                message,
                                null,
                                null
                            )
                            Log.d(TAG, "SMS sent to $contactName at $contactPhone")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to send SMS to $contactPhone", e)
                        }
                    } else {
                        Log.d(TAG, "SMS permission not granted")
                    }
                }
                
                // Also save notification to Firestore for in-app alerts
                val notification = hashMapOf(
                    "userId" to userId,
                    "contactId" to document.id,
                    "contactName" to contactName,
                    "contactPhone" to contactPhone,
                    "message" to message,
                    "type" to "sos",
                    "read" to false,
                    "timestamp" to com.google.firebase.Timestamp.now(),
                    "latitude" to location?.latitude,
                    "longitude" to location?.longitude,
                    "emergencyId" to emergencyId
                )
                
                firestore.collection("notifications")
                    .add(notification)
                    .addOnSuccessListener {
                        Log.d(TAG, "Notification saved to Firestore for $contactName")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error saving notification to Firestore", e)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SOS to contacts", e)
        }
    }

    /**
     * Send location update to emergency contacts.
     */
    private suspend fun sendLocationUpdateToContacts(location: Location) {
        try {
            val userId = userId ?: return
            val emergencyId = emergencyId ?: return
            
            // Get emergency contacts from Firestore
            val contactsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("emergency_contacts")
                .get()
                .await()
                
            if (contactsSnapshot.isEmpty) {
                Log.d(TAG, "No emergency contacts found for user $userId")
                return
            }
            
            // Create location update message
            val locationText = "Updated location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
            val message = "EMERGENCY UPDATE: $locationText"
            
            // For each contact, save an update notification to Firestore
            for (document in contactsSnapshot.documents) {
                val contactName = document.getString("name") ?: continue
                
                // Save location update to Firestore
                val locationUpdate = hashMapOf(
                    "emergencyId" to emergencyId,
                    "userId" to userId,
                    "contactId" to document.id,
                    "message" to message,
                    "type" to "location_update",
                    "timestamp" to com.google.firebase.Timestamp.now(),
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )
                
                firestore.collection("emergency_updates")
                    .add(locationUpdate)
                    .addOnSuccessListener {
                        Log.d(TAG, "Location update saved for $contactName")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error saving location update", e)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending location updates to contacts", e)
        }
    }

    /**
     * Create the foreground notification for the SOS service.
     */
    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_SOS)
        .setContentTitle("SOS Emergency Active")
        .setContentText("Sharing your location with emergency contacts")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java).apply {
                    action = ACTION_OPEN_SOS_SCREEN
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .addAction(
            R.drawable.ic_launcher_foreground,
            "Stop SOS",
            PendingIntent.getService(
                this,
                0,
                Intent(this, SOSService::class.java).apply { action = ACTION_STOP_SOS },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .build()

    companion object {
        private const val TAG = "SOSService"
        private const val NOTIFICATION_ID = 2001
        
        // Location update intervals (in milliseconds)
        private const val UPDATE_INTERVAL = 10000L // 10 seconds
        private const val FASTEST_INTERVAL = 5000L // 5 seconds
        private const val MAX_WAIT_TIME = 15000L // 15 seconds
        
        // Update emergency contacts every 2 minutes
        private const val UPDATE_CONTACTS_INTERVAL = 120000L // 2 minutes
        
        // Action constants
        const val ACTION_START_SOS = "com.the4codexlabs.smartugandanhealthcompanionappsuhc.action.START_SOS"
        const val ACTION_STOP_SOS = "com.the4codexlabs.smartugandanhealthcompanionappsuhc.action.STOP_SOS"
        const val ACTION_OPEN_SOS_SCREEN = "com.the4codexlabs.smartugandanhealthcompanionappsuhc.action.OPEN_SOS_SCREEN"
        
        // Extra constants
        const val EXTRA_USER_ID = "extra_user_id"
    }
}