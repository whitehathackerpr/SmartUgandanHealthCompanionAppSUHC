package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val language: String = "en",
    val theme: String = "system",
    val notificationsEnabled: Boolean = true,
    val age: Int = 0,
    val gender: String = "",
    val bloodType: String = "",
    val allergies: List<String> = emptyList(),
    val medicalConditions: List<String> = emptyList(),
    val medications: List<String> = emptyList(),
    val emergencyContacts: List<String> = emptyList(),
    val healthMetrics: Map<String, Any> = emptyMap(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) 