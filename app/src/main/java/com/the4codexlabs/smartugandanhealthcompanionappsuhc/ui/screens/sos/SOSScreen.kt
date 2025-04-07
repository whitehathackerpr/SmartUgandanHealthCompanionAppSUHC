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
        SOSContent(paddingValues = paddingValues)
    }
}

/**
 * SOS content composable.
 */
@Composable
fun SOSContent(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDarkTheme = isSystemInDarkTheme()
    
    // SOS state
    var isSosActive by remember { mutableStateOf(false) }
    var sosButtonPressStartTime by remember { mutableLongStateOf(0L) }
    var sosButtonPressed by remember { mutableStateOf(false) }
    
    // Emergency contacts state
    var emergencyContacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    var isLoadingContacts by remember { mutableStateOf(true) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    
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
        } else {
            // Not logged in, use empty list
            emergencyContacts = emptyList()
            isLoadingContacts = false
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // SOS Button
                Box(
                    modifier = Modifier
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow effect
                    if (isSosActive) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .scale(scale)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            SOSRed.copy(alpha = glowOpacity * 0.3f),
                                            SOSRed.copy(alpha = 0f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }
                    
                    // SOS Button
                    Surface(
                        modifier = Modifier
                            .size(if (isSosActive) 180.dp else 160.dp)
                            .scale(if (isSosActive) scale else 1f)
                            .shadow(
                                elevation = if (isSosActive) LargeElevation else MediumElevation,
                                shape = CircleShape
                            )
                            .clickable {
                                if (!isSosActive) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    sosButtonPressed = true
                                    sosButtonPressStartTime = System.currentTimeMillis()
                                }
                            },
                        shape = CircleShape,
                        color = if (isSosActive) {
                            SOSRed
                        } else {
                            SOSRed.copy(alpha = 0.9f)
                        },
                        contentColor = Color.White
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            SOSRed,
                                            SOSRed.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(56.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "SOS",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                // Instructions or status
                Text(
                    text = if (isSosActive) {
                        stringResource(id = R.string.sos_activated)
                    } else {
                        stringResource(id = R.string.press_for_sos)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                // Status message when active
                AnimatedVisibility(
                    visible = isSosActive,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.sos_sending),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = SOSRed,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // Cancel button
                        OutlinedButton(
                            onClick = {
                                isSosActive = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                
                                // Stop SOS service
                                val intent = android.content.Intent(context, SOSService::class.java).apply {
                                    action = SOSService.ACTION_STOP_SOS
                                }
                                context.startService(intent)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SOSRed
                            ),
                            border = BorderStroke(2.dp, SOSRed),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.shadow(4.dp, RoundedCornerShape(24.dp))
                        ) {
                            Text(
                                text = stringResource(id = R.string.sos_cancel),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        // Nearby Hospitals Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = MediumElevation,
                    shape = CardShape
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = CardShape
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.nearby_hospitals),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Map placeholder with enhanced styling
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(SmallElevation, RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Map will display nearby hospitals",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Additional info text
                Text(
                    text = "Your location will be shared with emergency services",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Emergency Contacts Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = MediumElevation,
                    shape = CardShape
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = CardShape
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.emergency_contacts),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Add contact button with enhanced styling
                    FloatingActionButton(
                        onClick = { showAddContactDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(SmallElevation, CircleShape),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Loading state
                if (isLoadingContacts) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (emergencyContacts.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No emergency contacts added yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Contact list with enhanced styling
                    emergencyContacts.forEach { contact ->
                        EmergencyContactItem(
                            contact = contact,
                            onCallClick = {
                                // Call the contact
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
                
                // Add a button to add more contacts
                Button(
                    onClick = { showAddContactDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .shadow(SmallElevation, RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SunGold,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Manage Emergency Contacts",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
    
    // Add Contact Dialog
    if (showAddContactDialog) {
        AddContactDialog(
            onDismiss = { showAddContactDialog = false },
            onAddContact = { name, phone, relationship ->
                // Add contact to Firestore
                val currentUser = FirebaseAuth.getInstance().currentUser
                
                if (currentUser != null) {
                    val newContact = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "relationship" to relationship
                    )
                    
                    FirebaseFirestore.getInstance().collection("users")
                        .document(currentUser.uid)
                        .collection("emergency_contacts")
                        .add(newContact)
                        .addOnSuccessListener {
                            // Refresh contacts list
                            isLoadingContacts = true
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
                        }
                }
                
                showAddContactDialog = false
            }
        )
    }
    
    // Handle SOS button press timing
    LaunchedEffect(sosButtonPressed) {
        if (sosButtonPressed) {
            delay(2000) // 2 seconds threshold for better UX
            if (System.currentTimeMillis() - sosButtonPressStartTime >= 2000) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isSosActive = true
                
                // Start SOS service
                val intent = android.content.Intent(context, SOSService::class.java).apply {
                    action = SOSService.ACTION_START_SOS
                    putExtra(SOSService.EXTRA_USER_ID, FirebaseAuth.getInstance().currentUser?.uid ?: "")
                }
                context.startService(intent)
            }
            sosButtonPressed = false
        }
    }
}

/**
 * Add Contact Dialog composable.
 */
@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAddContact: (name: String, phone: String, relationship: String) -> Unit
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
                modifier = Modifier.padding(16.dp),
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
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onAddContact(name, phone, relationship) },
                        enabled = name.isNotBlank() && phone.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

/**
 * Emergency contact item composable with enhanced styling.
 */
@Composable
fun EmergencyContactItem(
    contact: EmergencyContact,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = SmallElevation,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contact icon with enhanced styling
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(SmallElevation, CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Contact details with enhanced typography
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = contact.relationship,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Call button with enhanced styling
            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(SmallElevation, CircleShape)
                    .background(
                        color = SunGold,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
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