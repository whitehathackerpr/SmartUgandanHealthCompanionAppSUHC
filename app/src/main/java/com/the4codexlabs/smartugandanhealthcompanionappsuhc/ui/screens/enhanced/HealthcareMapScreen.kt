package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class HealthFacility(
    val name: String,
    val type: String,
    val distance: String,
    val address: String,
    val services: List<String>
)

@Composable
fun HealthcareMapScreen(navController: NavController) {
    val facilities = remember {
        listOf(
            HealthFacility(
                "Mulago National Referral Hospital",
                "Hospital",
                "2.5 km",
                "Upper Mulago Hill Road, Kampala",
                listOf("Emergency", "Surgery", "Maternity", "Pediatrics")
            ),
            HealthFacility(
                "Kawempe General Hospital",
                "Hospital",
                "4.8 km",
                "Kawempe, Kampala",
                listOf("Maternity", "Outpatient", "Laboratory")
            ),
            HealthFacility(
                "Nsambya Hospital",
                "Hospital",
                "5.2 km",
                "Nsambya Road, Kampala",
                listOf("Emergency", "Surgery", "Pediatrics")
            ),
            HealthFacility(
                "Mengo Medical Clinic",
                "Clinic",
                "3.1 km",
                "Mengo, Kampala",
                listOf("General Medicine", "Vaccination")
            )
        )
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Healthcare Map"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map Placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Map View",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search healthcare facilities") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            )

            // Nearby Facilities Section
            Text(
                text = "Nearby Healthcare Facilities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(facilities) { facility ->
                    FacilityCard(facility = facility)
                }
            }
        }
    }
}

@Composable
fun FacilityCard(facility: HealthFacility) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = facility.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = facility.type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(facility.distance) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = facility.address,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Services: ${facility.services.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { }) {
                    Icon(
                        Icons.Default.Directions,
                        contentDescription = "Directions"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions")
                }
                
                TextButton(onClick = { }) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Call"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }
            }
        }
    }
}