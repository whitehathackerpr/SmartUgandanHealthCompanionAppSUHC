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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
// Import the model class instead of redefining it
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Symptom

@Composable
fun SymptomTrackerScreen(
    navController: NavController,
    viewModel: SymptomTrackerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showErrorSnackbar = true
        }
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Symptom Tracker"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Time Range Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Time Range",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TimeRange.values().forEach { range ->
                            FilterChip(
                                selected = uiState.selectedTimeRange == range.name.lowercase(),
                                onClick = { viewModel.setTimeRange(range.name.lowercase()) },
                                label = { Text(range.name) }
                            )
                        }
                    }
                }
            }
            
            // Symptom Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                        text = "Symptom Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SymptomSummaryItem(
                            count = uiState.summaryData.totalSymptoms,
                            label = "Total Symptoms"
                        )
                        SymptomSummaryItem(
                            count = uiState.summaryData.maxSeverity,
                            label = "Max Severity"
                        )
                        SymptomSummaryItem(
                            count = uiState.summaryData.mostCommonSymptoms.size,
                            label = "Unique Symptoms"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Symptoms List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.symptoms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No symptoms recorded",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first symptom to start tracking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.symptoms.sortedByDescending { it.date }) { symptom ->
                        SymptomCard(
                            symptom = symptom,
                            onEdit = { viewModel.showAddSymptomDialog(true, symptom) },
                            onDelete = { viewModel.deleteSymptom(symptom.id) }
                        )
                    }
                }
            }
        }
        
        // FAB for adding new symptom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.showAddSymptomDialog(true) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Symptom")
            }
        }
    }
    
    // Add Symptom Dialog
    if (uiState.showAddSymptomDialog) {
        AddSymptomDialog(
            onDismiss = { viewModel.showAddSymptomDialog(false) },
            onSave = { name, severity, date, notes, relatedSymptoms ->
                viewModel.addSymptom(
                    name = name,
                    severity = severity,
                    date = date,
                    notes = notes,
                    relatedSymptoms = relatedSymptoms
                )
            },
            symptomToEdit = uiState.symptomToEdit
        )
    }
    
    // Error Snackbar
    if (showErrorSnackbar && uiState.error != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { 
                    showErrorSnackbar = false
                    viewModel.clearError()
                }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(uiState.error)
        }
    }
}

enum class TimeRange {
    WEEK, MONTH, YEAR, ALL
}

@Composable
fun SymptomSummaryItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SymptomCard(
    symptom: Symptom,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = symptom.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Severity Indicator
                SeverityIndicator(severity = symptom.severity)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(symptom.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (symptom.notes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = symptom.notes!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (symptom.relatedSymptoms?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Related Symptoms:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    symptom.relatedSymptoms?.forEach { relatedSymptom ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = relatedSymptom,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun SeverityIndicator(severity: Int) {
    val color = when {
        severity <= 3 -> Color(0xFF4CAF50) // Green
        severity <= 6 -> Color(0xFFFFC107) // Yellow
        severity <= 8 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "Severity: $severity",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun AddSymptomDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Date, String?, List<String>?) -> Unit,
    symptomToEdit: Symptom? = null
) {
    var name by remember { mutableStateOf(symptomToEdit?.name ?: "") }
    var severity by remember { mutableStateOf(symptomToEdit?.severity ?: 5) }
    var notes by remember { mutableStateOf(symptomToEdit?.notes ?: "") }
    var relatedSymptoms by remember { mutableStateOf(symptomToEdit?.relatedSymptoms ?: emptyList()) }
    var newRelatedSymptom by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (symptomToEdit != null) "Edit Symptom" else "Add Symptom") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Symptom Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Severity (1-10)")
                Slider(
                    value = severity.toFloat(),
                    onValueChange = { severity = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
                Text(
                    text = "Current: $severity",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Related Symptoms")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display current related symptoms
                if (relatedSymptoms.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        relatedSymptoms.forEach { symptom ->
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = symptom,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    
                                    IconButton(
                                        onClick = {
                                            relatedSymptoms = relatedSymptoms.filter { it != symptom }
                                        },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Add new related symptom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newRelatedSymptom,
                        onValueChange = { newRelatedSymptom = it },
                        label = { Text("Add Related Symptom") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    IconButton(
                        onClick = {
                            if (newRelatedSymptom.isNotBlank() && !relatedSymptoms.contains(newRelatedSymptom)) {
                                relatedSymptoms = relatedSymptoms + newRelatedSymptom
                                newRelatedSymptom = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        name,
                        severity,
                        symptomToEdit?.date ?: Date(),
                        notes.takeIf { it.isNotBlank() },
                        relatedSymptoms.takeIf { it.isNotEmpty() }
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 