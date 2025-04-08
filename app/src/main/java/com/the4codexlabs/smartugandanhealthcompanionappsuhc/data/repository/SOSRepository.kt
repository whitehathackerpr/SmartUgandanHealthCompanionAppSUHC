package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SmartUgandanHealthCompanionApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SOSRepository {
    private val auth = SmartUgandanHealthCompanionApp.auth
    private val firestore = SmartUgandanHealthCompanionApp.firestore
    private val sosCollection = firestore.collection("sos_alerts")

    suspend fun triggerSOS(
        location: Map<String, Double>,
        description: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
            
            // Get user profile for emergency contacts
            val userProfile = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)
                ?: throw IllegalStateException("User profile not found")
            
            // Create SOS alert
            val sosAlert = mapOf(
                "userId" to userId,
                "userName" to userProfile.name,
                "location" to location,
                "description" to description,
                "emergencyContacts" to userProfile.emergencyContacts,
                "medicalConditions" to userProfile.medicalConditions,
                "medications" to userProfile.medications,
                "bloodType" to userProfile.bloodType,
                "allergies" to userProfile.allergies,
                "status" to "active",
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )
            
            // Save SOS alert
            val alertId = sosCollection.add(sosAlert).await().id
            
            // Notify emergency contacts (TODO: Implement actual notification system)
            userProfile.emergencyContacts.forEach { contact ->
                // TODO: Send SMS or push notification to emergency contact
            }
            
            // Notify nearby healthcare providers (TODO: Implement actual notification system)
            // This would typically involve querying a database of healthcare providers
            // within a certain radius of the user's location
            
            Result.success(alertId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSOS(alertId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            sosCollection.document(alertId)
                .update(
                    mapOf(
                        "status" to "cancelled",
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveSOSAlerts(): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
            
            val alerts = sosCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "active")
                .get()
                .await()
                .documents
                .map { it.data ?: emptyMap() }
            
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 