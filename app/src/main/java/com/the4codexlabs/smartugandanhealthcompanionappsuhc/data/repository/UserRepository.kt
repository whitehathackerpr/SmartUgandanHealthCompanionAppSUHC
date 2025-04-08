package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SmartUgandanHealthCompanionApp
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class UserRepository {
    private val auth = SmartUgandanHealthCompanionApp.auth
    private val firestore = SmartUgandanHealthCompanionApp.firestore
    private val storage = FirebaseStorage.getInstance()
    private val usersCollection = firestore.collection("users")
    private val profileImagesRef = storage.reference.child("profile_images")

    fun getCurrentUser(): Flow<UserProfile?> = callbackFlow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
        
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                val userProfile = snapshot?.toObject(UserProfile::class.java)
                trySend(userProfile)
            }
            
        awaitClose { listener.remove() }
    }

    suspend fun updateUserProfile(userProfile: UserProfile): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val updatedProfile = userProfile.copy(updatedAt = Timestamp.now())
            
            firestore.collection("users")
                .document(userProfile.uid)
                .set(updatedProfile)
                .await()
            
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileImage(imageUri: Uri): String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
        val imageFileName = "${userId}_${UUID.randomUUID()}.jpg"
        val imageRef = profileImagesRef.child(imageFileName)
        
        // Upload the image
        imageRef.putFile(imageUri).await()
        
        // Get the download URL
        val downloadUrl = imageRef.downloadUrl.await().toString()
        
        // Update the user's profile with the new image URL
        usersCollection.document(userId)
            .update("photoUrl", downloadUrl, "updatedAt", System.currentTimeMillis())
            .await()
        
        return downloadUrl
    }

    suspend fun updateProfileInfo(
        name: String,
        phoneNumber: String,
        age: Int,
        gender: String,
        bloodType: String,
        allergies: List<String>,
        medicalConditions: List<String>,
        medications: List<String>,
        emergencyContacts: List<String>,
        healthMetrics: Map<String, Any>
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
            
            val updatedProfile = UserProfile(
                uid = userId,
                name = name,
                phoneNumber = phoneNumber,
                age = age,
                gender = gender,
                bloodType = bloodType,
                allergies = allergies,
                medicalConditions = medicalConditions,
                medications = medications,
                emergencyContacts = emergencyContacts,
                healthMetrics = healthMetrics,
                updatedAt = Timestamp.now()
            )
            
            usersCollection.document(userId)
                .set(updatedProfile, com.google.firebase.firestore.SetOptions.merge())
                .await()
            
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLanguage(language: String) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
        
        usersCollection.document(userId)
            .update("language", language, "updatedAt", System.currentTimeMillis())
            .await()
    }

    suspend fun updateTheme(theme: String) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
        
        usersCollection.document(userId)
            .update("theme", theme, "updatedAt", System.currentTimeMillis())
            .await()
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
        
        usersCollection.document(userId)
            .update("notificationsEnabled", enabled, "updatedAt", System.currentTimeMillis())
            .await()
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun createUserProfile(user: FirebaseUser): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val userProfile = UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                phoneNumber = user.phoneNumber ?: "",
                photoUrl = user.photoUrl?.toString() ?: "",
                language = "en",
                theme = "system",
                notificationsEnabled = true,
                age = 0,
                gender = "",
                bloodType = "",
                allergies = emptyList(),
                medicalConditions = emptyList(),
                medications = emptyList(),
                emergencyContacts = emptyList(),
                healthMetrics = emptyMap(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            firestore.collection("users")
                .document(user.uid)
                .set(userProfile)
                .await()
            
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 