package com.the4codexlabs.smartugandanhealthcompanionappsuhc.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.*

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // Existing routes
        composable("main") {
            // Main screen
        }
        
        // Enhanced features routes
        composable("enhanced_features") {
            EnhancedFeaturesScreen(navController)
        }
        
        composable("telemedicine") {
            TelemedicineScreen(navController)
        }
        
        composable("health_records") {
            HealthRecordsScreen(navController)
        }
        
        composable("symptom_tracker") {
            SymptomTrackerScreen(navController)
        }
        
        composable("medication_reminder") {
            MedicationReminderScreen(navController)
        }
        
        composable("health_education") {
            // Create and pass the ViewModel
            val healthEducationViewModel: HealthEducationViewModel = viewModel()
            HealthEducationScreen(healthEducationViewModel)
        }
        
        composable("community") {
            // Create and pass the ViewModel
            val communityViewModel: CommunityViewModel = viewModel()
            CommunityScreen(communityViewModel)
        }
        
        composable("wearables") {
            WearablesScreen(navController)
        }
        
        composable("healthcare_map") {
            HealthcareMapScreen(navController)
        }
        
        composable("analytics") {
            AnalyticsScreen(navController)
        }
    }
}