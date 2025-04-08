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
import androidx.hilt.navigation.compose.hiltViewModel
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model.Symptom
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SymptomScreen(
    viewModel: SymptomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showErrorSnackbar = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Symptom Tracker") },
                actions = {
                    IconButton(onClick = { viewModel.showAddSymptomDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Symptom")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.symptoms.isEmpty()) {
                EmptySymptomsList(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                SymptomsList(
                    symptoms = uiState.symptoms,
                    onEditSymptom = { viewModel.selectSymptom(it) },
                    onDeleteSymptom = { viewModel.deleteSymptom(it.id) }
                )
            }
        }

        if (uiState.showAddSymptomDialog) {
            AddSymptomDialog(
                symptom = uiState.selectedSymptom,
                onDismiss = { 
                    viewModel.hideAddSymptomDialog()
                    viewModel.clearSelectedSymptom()
                },
                onSave = { symptom ->
                    viewModel.saveSymptom(symptom)
                }
            )
        }

        if (showErrorSnackbar && uiState.error != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showErrorSnackbar = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(uiState.error)
            }
        }
    }
}

@Composable
private fun EmptySymptomsList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

@Composable
private fun SymptomsList(
    symptoms: List<Symptom>,
    onEditSymptom: (Symptom) -> Unit,
    onDeleteSymptom: (Symptom) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(symptoms.sortedByDescending { it.createdAt }) { symptom ->
            SymptomCard(
                symptom = symptom,
                onEdit = { onEditSymptom(symptom) },
                onDelete = { onDeleteSymptom(symptom) }
            )
        }
    }
}

@Composable
private fun SymptomCard(
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
                SeverityIndicator(severity = symptom.severity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (symptom.description.isNotEmpty()) {
                Text(
                    text = symptom.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (symptom.duration.isNotEmpty()) {
                        Text(
                            text = "Duration: ${symptom.duration}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (symptom.frequency.isNotEmpty()) {
                        Text(
                            text = "Frequency: ${symptom.frequency}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(symptom.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (symptom.associatedSymptoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Associated Symptoms:",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    symptom.associatedSymptoms.forEach { associatedSymptom ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = associatedSymptom,
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
private fun SeverityIndicator(severity: Int) {
    val color = when {
        severity <= 2 -> MaterialTheme.colorScheme.primary
        severity <= 4 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Surface(
        color = color.copy(alpha = 0.1f),
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
private fun AddSymptomDialog(
    symptom: Symptom?,
    onDismiss: () -> Unit,
    onSave: (Symptom) -> Unit
) {
    var name by remember { mutableStateOf(symptom?.name ?: "") }
    var description by remember { mutableStateOf(symptom?.description ?: "") }
    var severity by remember { mutableStateOf(symptom?.severity ?: 1) }
    var duration by remember { mutableStateOf(symptom?.duration ?: "") }
    var frequency by remember { mutableStateOf(symptom?.frequency ?: "") }
    var associatedSymptoms by remember { mutableStateOf(symptom?.associatedSymptoms ?: emptyList()) }
    var newAssociatedSymptom by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (symptom == null) "Add Symptom" else "Edit Symptom") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Symptom Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Column {
                    Text("Severity (1-5)")
                    Slider(
                        value = severity.toFloat(),
                        onValueChange = { severity = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text(
                        text = "Current: $severity",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text("Associated Symptoms")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (associatedSymptoms.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            associatedSymptoms.forEach { associatedSymptom ->
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = associatedSymptom,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        IconButton(
                                            onClick = {
                                                associatedSymptoms = associatedSymptoms.filter { it != associatedSymptom }
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newAssociatedSymptom,
                            onValueChange = { newAssociatedSymptom = it },
                            label = { Text("Add Associated Symptom") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (newAssociatedSymptom.isNotBlank() && !associatedSymptoms.contains(newAssociatedSymptom)) {
                                    associatedSymptoms = associatedSymptoms + newAssociatedSymptom
                                    newAssociatedSymptom = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Symptom(
                            id = symptom?.id ?: "",
                            name = name,
                            description = description,
                            severity = severity,
                            duration = duration,
                            frequency = frequency,
                            associatedSymptoms = associatedSymptoms,
                            notes = description,
                            createdAt = symptom?.createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
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