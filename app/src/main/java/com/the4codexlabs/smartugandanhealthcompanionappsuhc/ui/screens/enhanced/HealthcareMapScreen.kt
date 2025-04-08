package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.components.LoadingIndicator
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.components.SearchBar

data class HealthFacility(
    val name: String,
    val type: String,
    val distance: String,
    val address: String,
    val services: List<String>
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HealthcareMapScreen(
    viewModel: HealthcareMapViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (locationPermissionState.hasPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            viewModel.setLocationClient(fusedLocationClient)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Healthcare Facilities") },
                actions = {
                    IconButton(onClick = { /* TODO: Implement map view */ }) {
                        Icon(Icons.Default.Map, contentDescription = "Map View")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::searchFacilities,
                placeholder = "Search facilities..."
            )

            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.facilities) { facility ->
                        FacilityCard(
                            facility = facility,
                            onSelect = viewModel::selectFacility
                        )
                    }
                }
            }
        }

        // Error handling
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }

        // Selected facility dialog
        uiState.selectedFacility?.let { facility ->
            FacilityDetailsDialog(
                facility = facility,
                onDismiss = { viewModel.selectFacility(null) }
            )
        }
    }
}

@Composable
private fun FacilityCard(
    facility: HealthFacility,
    onSelect: (HealthFacility) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(facility) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = facility.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = facility.type,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = facility.address,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (facility.distance > 0) {
                Text(
                    text = "%.1f km away".format(facility.distance / 1000),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FacilityDetailsDialog(
    facility: HealthFacility,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(facility.name) },
        text = {
            Column {
                Text("Type: ${facility.type}")
                Text("Address: ${facility.address}")
                if (facility.phone != null) {
                    Text("Phone: ${facility.phone}")
                }
                if (facility.email != null) {
                    Text("Email: ${facility.email}")
                }
                if (facility.website != null) {
                    Text("Website: ${facility.website}")
                }
                
                if (facility.services.isNotEmpty()) {
                    Text("Services:", style = MaterialTheme.typography.titleSmall)
                    facility.services.forEach { service ->
                        Text("• $service")
                    }
                }

                if (facility.operatingHours.isNotEmpty()) {
                    Text("Operating Hours:", style = MaterialTheme.typography.titleSmall)
                    facility.operatingHours.forEach { (day, hours) ->
                        Text("$day: $hours")
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = {
                        facility.phone?.let { phone ->
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }
                TextButton(
                    onClick = {
                        facility.location?.let { location ->
                            val uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${facility.name}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}