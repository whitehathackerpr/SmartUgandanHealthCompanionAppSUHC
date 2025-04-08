package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.sos

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.services.SOSService
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SOSRed
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GlassLight
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GlassDark
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.CardShape
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SOSButtonShape
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.LargeElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.MediumElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmallElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SunGold
import kotlinx.coroutines.delay

/**
 * Data class representing an emergency contact.
 */
data class EmergencyContact(
    val name: String,
    val phone: String,
    val relationship: String
)

/**
 * Data class representing a nearby hospital.
 */
data class NearbyHospital(
    val name: String,
    val distance: String,
    val address: String,
    val phone: String
)

/**
 * SOS Emergency screen composable.
 * Allows users to trigger an emergency alert and send their location to emergency contacts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sos_emergency),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SOSRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        SOSContent(
            paddingValues = paddingValues,
            onNavigateBack = { navController.navigateUp() },
            onNavigateToHealthcareMap = { navController.navigate("healthcare_map") }
        )
    }
}

/**
 * SOS content composable.
 */
@Composable
fun SOSContent(
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    onNavigateToHealthcareMap: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDarkTheme = isSystemInDarkTheme()
    
    // SOS state
    var isSosActive by remember { mutableStateOf(false) }
    var sosButtonPressStartTime by remember { mutableLongStateOf(0L) }
    var sosButtonPressed by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var pressProgress by remember { mutableStateOf(0f) }
    
    // Emergency contacts state
    var emergencyContacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    var isLoadingContacts by remember { mutableStateOf(true) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    
    // Nearby hospitals state
    var nearbyHospitals by remember { mutableStateOf<List<NearbyHospital>>(emptyList()) }
    var isLoadingHospitals by remember { mutableStateOf(true) }
    
    // Fetch emergency contacts from Firestore
    LaunchedEffect(key1 = true) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .collection("emergency_contacts")
                .get()
                .addOnSuccessListener { documents ->
                    val contacts = documents.map { document ->
                        EmergencyContact(
                            name = document.getString("name") ?: "",
                            phone = document.getString("phone") ?: "",
                            relationship = document.getString("relationship") ?: ""
                        )
                    }
                    emergencyContacts = contacts
                    isLoadingContacts = false
                }
                .addOnFailureListener {
                    // Use empty list on failure
                    emergencyContacts = emptyList()
                    isLoadingContacts = false
                }
                
            // Fetch nearby hospitals
            FirebaseFirestore.getInstance().collection("health_facilities")
                .limit(3)
                .get()
                .addOnSuccessListener { documents ->
                    val hospitals = documents.map { document ->
                        NearbyHospital(
                            name = document.getString("name") ?: "Unknown Hospital",
                            distance = document.getString("distance") ?: "Unknown distance",
                            address = document.getString("address") ?: "Unknown address",
                            phone = document.getString("phone") ?: ""
                        )
                    }
                    nearbyHospitals = hospitals
                    isLoadingHospitals = false
                }
                .addOnFailureListener {
                    // Use empty list on failure
                    nearbyHospitals = emptyList()
                    isLoadingHospitals = false
                }
        } else {
            // Not logged in, use empty list
            emergencyContacts = emptyList()
            isLoadingContacts = false
            nearbyHospitals = emptyList()
            isLoadingHospitals = false
        }
    }
    
    // Pulsating animation for SOS button
    val infiniteTransition = rememberInfiniteTransition(label = "SOSPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SOSPulseScale"
    )
    
    // Opacity animation for glow effect
    val glowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SOSGlowOpacity"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // SOS Button Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = LargeElevation,
                    shape = CardShape
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) 
                    GlassDark 
                else 
                    GlassLight,
                contentColor = if (isSosActive) SOSRed else MaterialTheme.colorScheme.onSurface
            ),
            shape = CardShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SOS Button
                Box(
                    modifier = Modifier
                        .size(if (isSosActive) 180.dp else 160.dp)
                        .scale(if (isSosActive) scale else 1f)
                        .shadow(
                            elevation = if (isSosActive) LargeElevation else MediumElevation,
                            shape = SOSButtonShape
                        )
                        .clip(SOSButtonShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isSosActive) {
                                    listOf(
                                        SOSRed,
                                        SOSRed.copy(alpha = 0.9f)
                                    )
                                } else {
                                    listOf(
                                        SOSRed.copy(alpha = 0.9f),
                                        SOSRed.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        )
                        .clickable(
                            enabled = !isSosActive,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                sosButtonPressed = true
                                sosButtonPressStartTime = System.currentTimeMillis()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow effect
                    if (isSosActive) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            SOSRed.copy(alpha = glowOpacity * 0.3f),
                                            SOSRed.copy(alpha = 0f)
                                        )
                                    )
                                )
                        )
                    }
                    
                    // SOS Button
                    Box(
                        modifier = Modifier
                            .size(if (isSosActive) 180.dp else 160.dp)
                            .scale(if (isSosActive) scale else 1f)
                            .shadow(
                                elevation = if (isSosActive) LargeElevation else MediumElevation,
                                shape = SOSButtonShape
                            )
                            .clip(SOSButtonShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (isSosActive) {
                                        listOf(
                                            SOSRed,
                                            SOSRed.copy(alpha = 0.8f)
                                        )
                                    } else {
                                        listOf(
                                            SOSRed.copy(alpha = 0.9f),
                                            SOSRed.copy(alpha = 0.7f)
                                        )
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "SOS",
                            modifier = Modifier.size(80.dp),
                            tint = Color.White
                        )
                        
                        Text(
                            text = "SOS",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Press progress indicator (only visible when pressing)
                if (sosButtonPressed && !isSosActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pressProgress)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SOSRed)
                        )
                    }
                }
                
                // Instructions text
                Text(
                    text = if (isSosActive) {
                        stringResource(id = R.string.sos_activated)
                    } else {
                        stringResource(id = R.string.press_for_sos)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isSosActive) SOSRed else MaterialTheme.colorScheme.onSurface
                )
                
                // Cancel button (only visible when SOS is active)
                AnimatedVisibility(
                    visible = isSosActive,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SOSRed
                        ),
                        border = BorderStroke(2.dp, SOSRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.sos_cancel))
                    }
                }
                
                // Loading indicator (only visible when sending SOS)
                AnimatedVisibility(
                    visible = isSosActive && !showCancelDialog,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SOSRed,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Text(
                            text = stringResource(id = R.string.sos_sending),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SOSRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Emergency Contacts Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = SmallElevation,
                    shape = CardShape
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) 
                    GlassDark 
                else 
                    GlassLight
            ),
            shape = CardShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Emergency Contacts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    FloatingActionButton(
                        onClick = { showAddContactDialog = true },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                if (isLoadingContacts) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (emergencyContacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No emergency contacts added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    emergencyContacts.forEach { contact ->
                        EmergencyContactItem(contact = contact)
                    }
                }
            }
        }
        
        // Nearby Hospitals Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = SmallElevation,
                    shape = CardShape
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) 
                    GlassDark 
                else 
                    GlassLight
            ),
            shape = CardShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Hospitals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Button(
                        onClick = onNavigateToHealthcareMap,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("View All")
                    }
                }
                
                if (isLoadingHospitals) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (nearbyHospitals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No nearby hospitals found",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    nearbyHospitals.forEach { hospital ->
                        HospitalItem(hospital = hospital)
                    }
                }
            }
        }
    }
    
    // Add Contact Dialog
    if (showAddContactDialog) {
        AddContactDialog(
            onDismiss = { showAddContactDialog = false },
            onSave = { name, phone, relationship ->
                // Save contact to Firestore
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val contact = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "relationship" to relationship
                    )
                    
                    FirebaseFirestore.getInstance().collection("users")
                        .document(currentUser.uid)
                        .collection("emergency_contacts")
                        .add(contact)
                        .addOnSuccessListener {
                            // Add to local list
                            emergencyContacts = emergencyContacts + EmergencyContact(name, phone, relationship)
                            showAddContactDialog = false
                        }
                }
            }
        )
    }
    
    // Cancel SOS Dialog
    if (showCancelDialog) {
        Dialog(onDismissRequest = { showCancelDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Cancel SOS Alert?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Are you sure you want to cancel the SOS alert? This will stop sending your location to emergency contacts.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCancelDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("No, Keep Active")
                        }
                        
                        Button(
                            onClick = {
                                isSosActive = false
                                showCancelDialog = false
                                
                                // Stop SOS service
                                val intent = Intent(context, SOSService::class.java).apply {
                                    action = SOSService.ACTION_STOP_SOS
                                }
                                context.startService(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SOSRed
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Yes, Cancel SOS")
                        }
                    }
                }
            }
        }
    }
    
    // Handle SOS button press timing
    LaunchedEffect(sosButtonPressed) {
        if (sosButtonPressed) {
            // Animate progress over 3 seconds
            val startTime = System.currentTimeMillis()
            val duration = 3000L
            
            while (sosButtonPressed && System.currentTimeMillis() - startTime < duration) {
                val elapsed = System.currentTimeMillis() - startTime
                pressProgress = (elapsed.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                delay(16) // ~60fps
            }
            
            if (sosButtonPressed) {
                // Button was held for 3 seconds, activate SOS
                isSosActive = true
                sosButtonPressed = false
                pressProgress = 0f
                
                // Start SOS service
                val intent = Intent(context, SOSService::class.java).apply {
                    action = SOSService.ACTION_START_SOS
                    putExtra(SOSService.EXTRA_USER_ID, FirebaseAuth.getInstance().currentUser?.uid)
                }
                context.startService(intent)
            } else {
                // Button was released before 3 seconds
                pressProgress = 0f
            }
        }
    }
}

/**
 * Emergency contact item composable.
 */
@Composable
fun EmergencyContactItem(contact: EmergencyContact) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = SmallElevation,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Column {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = contact.relationship,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${contact.phone}")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Call",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Hospital item composable.
 */
@Composable
fun HospitalItem(hospital: NearbyHospital) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = SmallElevation,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                Column {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = hospital.distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = hospital.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            if (hospital.phone.isNotBlank()) {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${hospital.phone}")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Call",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Add contact dialog composable.
 */
@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relationship: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Emergency Contact",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { onSave(name, phone, relationship) },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && phone.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

/**
 * Preview function for the SOS screen.
 */
@Preview(showBackground = true)
@Composable
fun SOSScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SOSScreen(rememberNavController())
        }
    }
}