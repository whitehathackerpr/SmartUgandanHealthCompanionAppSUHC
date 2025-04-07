package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.diagnosis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme

/**
 * Data class representing a diagnosis result.
 */
data class DiagnosisResultFixed2(
    val condition: String,
    val recommendation: String,
    val confidence: Int
)

/**
 * Diagnosis (Symptom Checker) screen composable.
 * Allows users to enter symptoms and receive a diagnosis with recommendations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreenFixed2(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.symptom_checker),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        DiagnosisContentFixed2(paddingValues = paddingValues, snackbarHostState = snackbarHostState)
    }
}

/**
 * Diagnosis content composable.
 */
@Composable
fun DiagnosisContentFixed2(paddingValues: PaddingValues, snackbarHostState: SnackbarHostState) {
    // State for symptoms and diagnosis
    val symptoms = remember { mutableStateListOf<String>() }
    var currentSymptom by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var diagnosisResult by remember { mutableStateOf<DiagnosisResultFixed2?>(null) }
    
    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Symptoms Input Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enter_symptoms),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Symptom input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = currentSymptom,
                        onValueChange = { currentSymptom = it },
                        label = { Text(stringResource(id = R.string.add_symptom)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    IconButton(
                        onClick = {
                            if (currentSymptom.isNotBlank() && !symptoms.contains(currentSymptom)) {
                                symptoms.add(currentSymptom)
                                currentSymptom = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Symptom",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                // Symptom chips
                if (symptoms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        symptoms.forEachIndexed { index, symptom ->
                            SymptomChipFixed2(
                                symptom = symptom,
                                onRemove = { symptoms.removeAt(index) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Duration input
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text(stringResource(id = R.string.symptom_duration)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
        
        // Check Diagnosis Button
        Button(
            onClick = {
                if (symptoms.isNotEmpty()) {
                    isLoading = true
                    
                    // Use coroutine to make the API call
                    coroutineScope.launch {
                        try {
                            val request = com.the4codexlabs.smartugandanhealthcompanionappsuhc.api.DiagnosisRequest(
                                symptoms = symptoms.toList()
                            )
                            
                            // Use coroutine to call the suspend function
                            val response = com.the4codexlabs.smartugandanhealthcompanionappsuhc.api.DiagnosisApi.service.diagnose(request)
                            
                            if (response.isSuccessful && response.body() != null) {
                                val apiResponse = response.body()!!
                                diagnosisResult = DiagnosisResultFixed2(
                                    condition = apiResponse.condition,
                                    recommendation = apiResponse.recommendation,
                                    confidence = apiResponse.confidence
                                )
                            } else {
                                // Handle error with snackbar instead of Toast
                                val errorMessage = response.message() ?: "Unknown error"
                                snackbarHostState.showSnackbar("Error: $errorMessage")
                            }
                        } catch (e: Exception) {
                            // Handle exception with snackbar instead of Toast
                            val errorMessage = e.message ?: "Unknown error"
                            snackbarHostState.showSnackbar("Error: $errorMessage")
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = symptoms.isNotEmpty() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.check_diagnosis))
            }
        }
        
        // Diagnosis Result Section
        AnimatedVisibility(
            visible = diagnosisResult != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            diagnosisResult?.let { result ->
                DiagnosisResultCardFixed2(result = result)
            }
        }
        
        // Disclaimer
        Text(
            text = stringResource(id = R.string.disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Symptom chip composable.
 */
@Composable
fun SymptomChipFixed2(
    symptom: String,
    onRemove: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(50),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = symptom,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Symptom",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Confidence indicator composable.
 * Displays a progress bar with color based on confidence level.
 */
@Composable
private fun ConfidenceIndicatorFixed2(confidence: Int) {
    // Pre-calculate progress value
    val progressValue = confidence / 100f
    
    // Use hardcoded colors instead of MaterialTheme.colorScheme
    val progressColor = if (confidence > 70) {
        Color(0xFF6200EE) // Purple
    } else {
        Color(0xFFB00020) // Red
    }
    
    LinearProgressIndicator(
        progress = progressValue,
        modifier = Modifier.fillMaxWidth(),
        color = progressColor
    )
}

/**
 * Diagnosis result card composable.
 */
@Composable
fun DiagnosisResultCardFixed2(result: DiagnosisResultFixed2) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.diagnosis_results),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Possible condition
            Column {
                Text(
                    text = stringResource(id = R.string.possible_condition),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                
                Text(
                    text = result.condition,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Recommendation
            Column {
                Text(
                    text = stringResource(id = R.string.recommendation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                
                Text(
                    text = result.recommendation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Confidence
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.confidence),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "${result.confidence}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                ConfidenceIndicatorFixed2(confidence = result.confidence)
            }
        }
    }
}

/**
 * Preview function for the diagnosis screen.
 */
@Preview(showBackground = true)
@Composable
fun DiagnosisScreenFixed2Preview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DiagnosisScreenFixed2(rememberNavController())
        }
    }
}