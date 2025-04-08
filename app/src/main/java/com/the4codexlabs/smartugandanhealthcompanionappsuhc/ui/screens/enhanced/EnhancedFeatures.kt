package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed class EnhancedFeature(
    val title: String,
    val description: String,
    val route: String
) {
    object Telemedicine : EnhancedFeature(
        "Telemedicine",
        "Connect with healthcare professionals remotely",
        "telemedicine"
    )
    object HealthRecords : EnhancedFeature(
        "Health Records",
        "Secure storage for your medical history",
        "health_records"
    )
    object SymptomTracker : EnhancedFeature(
        "Symptom Tracker",
        "Track and analyze your symptoms over time",
        "symptom_tracker"
    )
    object MedicationReminder : EnhancedFeature(
        "Medication Reminder",
        "Never miss your medication schedule",
        "medication_reminder"
    )
    object HealthEducation : EnhancedFeature(
        "Health Education",
        "Access health information in your language",
        "health_education"
    )
    object Community : EnhancedFeature(
        "Community",
        "Connect with others and share experiences",
        "community"
    )
    object Wearables : EnhancedFeature(
        "Wearables",
        "Connect and sync your health devices",
        "wearables"
    )
    object HealthcareMap : EnhancedFeature(
        "Healthcare Map",
        "Find nearby healthcare facilities",
        "healthcare_map"
    )
    object Analytics : EnhancedFeature(
        "Health Analytics",
        "Track your health progress over time",
        "analytics"
    )
}