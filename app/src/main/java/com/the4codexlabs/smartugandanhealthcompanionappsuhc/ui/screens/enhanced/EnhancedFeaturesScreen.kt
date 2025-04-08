package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.components.EnhancedFeatureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedFeaturesScreen(navController: NavController) {
    val features = listOf(
        EnhancedFeature.Telemedicine,
        EnhancedFeature.HealthRecords,
        EnhancedFeature.SymptomTracker,
        EnhancedFeature.MedicationReminder,
        EnhancedFeature.HealthEducation,
        EnhancedFeature.Community,
        EnhancedFeature.Wearables,
        EnhancedFeature.HealthcareMap,
        EnhancedFeature.Analytics
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enhanced Features") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Welcome Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to SUHC Enhanced Features",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Access all your health management tools in one place",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Features Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(features) { feature ->
                    EnhancedFeatureCard(
                        feature = feature,
                        onClick = { navController.navigate(feature.route) }
                    )
                }
            }
        }
    }
} 