package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Edit Profile Screen composable.
 * Allows users to edit their profile information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    
    // State for user profile data
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var medicalConditions by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    
    // State for dropdown menus
    var expandedGender by remember { mutableStateOf(false) }
    var expandedBloodType by remember { mutableStateOf(false) }
    
    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    
    // Fetch user profile data
    LaunchedEffect(key1 = true) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            try {
                val document = FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                if (document != null && document.exists()) {
                    // Extract user profile data
                    name = document.getString("name") ?: currentUser.displayName ?: ""
                    age = document.getLong("age")?.toString() ?: ""
                    gender = document.getString("gender") ?: ""
                    bloodType = document.getString("bloodType") ?: ""
                    
                    // Extract lists
                    val allergiesList = document.get("allergies") as? List<String>
                    allergies = allergiesList?.joinToString(", ") ?: ""
                    
                    val medicalConditionsList = document.get("medicalConditions") as? List<String>
                    medicalConditions = medicalConditionsList?.joinToString(", ") ?: ""
                    
                    val medicationsList = document.get("medications") as? List<String>
                    medications = medicationsList?.joinToString(", ") ?: ""
                }
            } catch (e: Exception) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error loading profile: ${e.message}")
                }
            } finally {
                isLoading = false
            }
        } else {
            // Not logged in
            isLoading = false
            coroutineScope.launch {
                snackbarHostState.showSnackbar("You must be logged in to edit your profile")
                navController.navigateUp()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_profile),
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
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isSaving = true
                                try {
                                    val result = saveProfile(
                                        name = name,
                                        age = age,
                                        gender = gender,
                                        bloodType = bloodType,
                                        allergies = allergies,
                                        medicalConditions = medicalConditions,
                                        medications = medications
                                    )
                                    
                                    if (result.isSuccess) {
                                        snackbarHostState.showSnackbar("Profile updated successfully")
                                        navController.navigateUp()
                                    } else {
                                        snackbarHostState.showSnackbar("Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
                                    }
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditProfileContent(
                paddingValues = paddingValues,
                name = name,
                onNameChange = { name = it },
                age = age,
                onAgeChange = { age = it },
                gender = gender,
                onGenderChange = { gender = it },
                expandedGender = expandedGender,
                onExpandedGenderChange = { expandedGender = it },
                bloodType = bloodType,
                onBloodTypeChange = { bloodType = it },
                expandedBloodType = expandedBloodType,
                onExpandedBloodTypeChange = { expandedBloodType = it },
                allergies = allergies,
                onAllergiesChange = { allergies = it },
                medicalConditions = medicalConditions,
                onMedicalConditionsChange = { medicalConditions = it },
                medications = medications,
                onMedicationsChange = { medications = it },
                isSaving = isSaving,
                onSaveClick = {
                    coroutineScope.launch {
                        isSaving = true
                                try {
                                    saveProfile(
                                        name = name,
                                        age = age,
                                        gender = gender,
                                        bloodType = bloodType,
                                        allergies = allergies,
                                        medicalConditions = medicalConditions,
                                        medications = medications
                                    )
                                    // Success handling is done in the Scaffold
                        } finally {
                            isSaving = false
                        }
                    }
                },
                focusManager = focusManager
            )
        }
    }
}

/**
 * Edit Profile Content composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    paddingValues: PaddingValues,
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    expandedGender: Boolean,
    onExpandedGenderChange: (Boolean) -> Unit,
    bloodType: String,
    onBloodTypeChange: (String) -> Unit,
    expandedBloodType: Boolean,
    onExpandedBloodTypeChange: (Boolean) -> Unit,
    allergies: String,
    onAllergiesChange: (String) -> Unit,
    medicalConditions: String,
    onMedicalConditionsChange: (String) -> Unit,
    medications: String,
    onMedicationsChange: (String) -> Unit,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Personal Information Card
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.personal_info),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Divider()
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(id = R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
                
                // Age field
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) onAgeChange(it) },
                    label = { Text(stringResource(id = R.string.age)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
                
                // Gender dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedGender,
                    onExpandedChange = onExpandedGenderChange
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.gender)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isSaving
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedGender,
                        onDismissRequest = { onExpandedGenderChange(false) }
                    ) {
                        listOf("Male", "Female", "Other", "Prefer not to say").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onGenderChange(option)
                                    onExpandedGenderChange(false)
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        }
                    }
                }
                
                // Blood Type dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedBloodType,
                    onExpandedChange = onExpandedBloodTypeChange
                ) {
                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.blood_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isSaving
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedBloodType,
                        onDismissRequest = { onExpandedBloodTypeChange(false) }
                    ) {
                        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onBloodTypeChange(option)
                                    onExpandedBloodTypeChange(false)
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Medical Information Card
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Medical Information",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Divider()
                
                // Allergies field
                OutlinedTextField(
                    value = allergies,
                    onValueChange = onAllergiesChange,
                    label = { Text(stringResource(id = R.string.allergies)) },
                    placeholder = { Text("Separate with commas") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !isSaving
                )
                
                // Medical Conditions field
                OutlinedTextField(
                    value = medicalConditions,
                    onValueChange = onMedicalConditionsChange,
                    label = { Text(stringResource(id = R.string.medical_conditions)) },
                    placeholder = { Text("Separate with commas") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !isSaving
                )
                
                // Medications field
                OutlinedTextField(
                    value = medications,
                    onValueChange = onMedicationsChange,
                    label = { Text(stringResource(id = R.string.medications)) },
                    placeholder = { Text("Separate with commas") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    enabled = !isSaving
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Save button
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.save))
            }
        }
    }
}

/**
 * Save profile data to Firestore.
 * @return Result<Unit> indicating success or failure
 */
private suspend fun saveProfile(
    name: String,
    age: String,
    gender: String,
    bloodType: String,
    allergies: String,
    medicalConditions: String,
    medications: String
): Result<Unit> {
    val currentUser = FirebaseAuth.getInstance().currentUser
        ?: return Result.failure(Exception("You must be logged in to save your profile"))
    
    // Convert comma-separated strings to lists
    val allergiesList = allergies.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val medicalConditionsList = medicalConditions.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val medicationsList = medications.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    
    // Create profile data
    val profileData = hashMapOf(
        "name" to name,
        "age" to if (age.isNotEmpty()) age.toInt() else 0,
        "gender" to gender,
        "bloodType" to bloodType,
        "allergies" to allergiesList,
        "medicalConditions" to medicalConditionsList,
        "medications" to medicationsList,
        "updatedAt" to com.google.firebase.Timestamp.now()
    )
    
    // Save to Firestore using coroutines
    return try {
        FirebaseFirestore.getInstance().collection("users")
            .document(currentUser.uid)
            .update(profileData as Map<String, Any>)
            .await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}